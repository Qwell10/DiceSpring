const diceArea = document.getElementById("diceArea");
const gameMessage = document.getElementById("gameMessage");
const rollBtn = document.getElementById("rollBtn");
const scoreBtn = document.getElementById("scoreBtn");
const endTurnBtn = document.getElementById("endTurnBtn");

let myPlayerId = 0;
let myRole = "";
let stompClient = null;

async function initializeGame() {
    try {
        console.log("Odesílám žádost o registraci...");

        const response = await fetch("/api/dice/join", {
            method: "POST"
        });

        const data = await response.json();

        myPlayerId = data.id;
        myRole = data.role;

        console.log(`Úspěšně zaregistrován! Moje ID: ${myPlayerId}, Role: ${myRole}`);

        connect();

    } catch (error) {
        console.error("Chyba při registraci hráče:", error);
    }
}

initializeGame();

function connect() {
  updateButtonsUI(1)

  const socket = new SockJS("/ws");
  stompClient = Stomp.over(socket);

  const headers = {
    playerId: myPlayerId,
  };

  stompClient.connect(
    headers,
    function (frame) {
      console.log("✅ WebSocket připojen: " + frame);

      stompClient.subscribe("/topic/player-status", function (statusMessage) {
        const status = JSON.parse(statusMessage.body);
        updatePlayerStatusUI(status.isPlayer1Connected, status.isPlayer2Connected);
      });

      stompClient.subscribe("/topic/game-state", function (message) {
        const gameState = JSON.parse(message.body);
        console.log("Nový stav hry ze serveru:", gameState);

        updateButtonsUI(gameState.activePlayerId);

        // Pokud server poslal nějaké kostky a já NEJSEM na tahu, vykreslím je jako divák
        if (gameState.rolledDice && gameState.rolledDice.length > 0 && myPlayerId !== gameState.activePlayerId) {
            // allowSelection = false -> divák na ně nemůže klikat
            renderDice(gameState.rolledDice, false, false); 
        }
      });

      fetch("/api/dice/status")
        .then((response) => response.json())
        .then((status) => {
          console.log("📥 Načten úvodní stav:", status);
          updatePlayerStatusUI(
            status.isPlayer1Connected,
            status.isPlayer2Connected,
          );
        })
        .catch((error) =>
          console.error("❌ Chyba při načítání úvodního stavu:", error),
        );
    },
    function (error) {
      console.error("❌ Chyba WebSocketu: " + error);
    },
  );
}

function switchActivePlayerUI() {
  const player1Box = document.getElementById("player1-card");
  const player2Box = document.getElementById("player2-card");

  player1Box.classList.toggle("active");
  player2Box.classList.toggle("active");
}

function updatePlayerStatusUI(p1Connected, p2Connected) {
    const card1 = document.getElementById("player1-card");
    const statusP1 = document.getElementById("status-p1");
    const dotP1 = document.getElementById("dot-p1");

    const card2 = document.getElementById("player2-card");
    const statusP2 = document.getElementById("status-p2");
    const dotP2 = document.getElementById("dot-p2");

    if (p1Connected) {
        card1.style.opacity = "1"; 
        card1.style.border = "2px solid #2ecc71"; 

        statusP1.innerText = "Připojen";
        dotP1.classList.replace('offline', 'online');
    } else {
        card1.style.opacity = "0.5"; 
        card1.style.border = "2px solid #e74c3c";

        statusP1.innerText = "Čeká se na připojení...";
        dotP1.classList.replace('online', 'offline');
    }

    if (p2Connected) {
        card2.style.opacity = "1";
        card2.style.border = "2px solid #2ecc71";
        
        statusP2.innerText = "Připojen";
        dotP2.classList.replace('offline', 'online');
    } else {
        card2.style.opacity = "0.5";
        card2.style.border = "2px solid #e74c3c";
        
        statusP2.innerText = "Čeká se na připojení...";
        dotP2.classList.replace('online', 'offline');
    }
}

function updateButtonsUI(activePlayerId) {
    if (myPlayerId === activePlayerId) {
        rollBtn.disabled = false; 
    } else {
        rollBtn.disabled = true;
        scoreBtn.disabled = true;
        endTurnBtn.disabled = true;
    }
}

function renderDice(diceValues, isBust, allowSelection) {
  // 1. Vyčistíme stůl před novým hodem
  diceArea.innerHTML = "";
  const diceElements = [];

  // 2. Vytvoříme kostky s otazníkem a animací točení
  for (let i = 0; i < diceValues.length; i++) {
    const die = document.createElement("div");
    die.className = "die rolling";
    die.innerText = "?";
    diceArea.appendChild(die);
    diceElements.push(die);
  }

  // 3. Po 600ms zastavíme animaci a ukážeme reálná čísla
  setTimeout(() => {
    diceElements.forEach((die, index) => {
      die.classList.remove("rolling");
      die.innerText = diceValues[index];

      // Pokud hráč přetáhl (bust), obarvíme kostku
      if (isBust === true) {
        die.classList.add("bust-die");
      } 
      // Pokud nepřetáhl a MÁ právo vybírat (jeho tah), přidáme klikání
      else if (allowSelection === true) {
        die.addEventListener("click", () => {
          die.classList.toggle("selected");
          const selectedCount = document.querySelectorAll(".die.selected").length;

          if (selectedCount > 0) {
            scoreBtn.disabled = false;
          } else {
            scoreBtn.disabled = true;
          }
        });
      }
    });
  }, 600);
}

function showMessage(text, isError) {
  gameMessage.innerText = text;

  if (isError) {
    gameMessage.className = "message-error";
  } else {
    gameMessage.className = "message-success";
  }

  setTimeout(() => {
    gameMessage.style.display = "none";
    gameMessage.className = "";
  }, 3500);
}

rollBtn.addEventListener("click", () => {
  rollBtn.disabled = true;
  scoreBtn.disabled = true;
  endTurnBtn.disabled = true;

  fetch("/api/dice/roll", { method: "POST" })
    .then((response) => response.json())
    .then((data) => {
      
      renderDice(data.dice, data.isBust, true);

      if (data.isBust === true) {
        setTimeout(() => {
          showMessage(data.message, true);
          document.getElementById("actual-score-p1").innerText = "0";
          document.getElementById("actual-score-p2").innerText = "0";
        }, 600);

        setTimeout(() => {
          diceArea.innerHTML = "";
          switchActivePlayerUI();
          rollBtn.disabled = false;
        }, 3600);
      }
    })
    .catch((error) => {
      console.error("Chyba spojení s Javou:", error);
      showMessage(error.message, true);
      rollBtn.disabled = false;
    });
});

scoreBtn.addEventListener("click", () => {
  const selectedElements = document.querySelectorAll(".die.selected");
  const selectedDice = [];

  selectedElements.forEach((die) => {
    selectedDice.push(parseInt(die.innerText));
  });

  console.log("Selected dice to send:", selectedDice);

  scoreBtn.disabled = true;

  fetch("/api/dice/score", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(selectedDice),
  })
    .then((response) => {
      return response.json().then((data) => {
        if (!response.ok) {
          throw new Error(
            data.errorMessage || data.message || "Neznámá chyba serveru ???",
          );
        }
        return data;
      });
    })
    .then((turnStatus) => {
      const isPlayer1Active = document
        .getElementById("player1-card")
        .classList.contains("active");
      const targetSpanId = isPlayer1Active
        ? "actual-score-p1"
        : "actual-score-p2";

      document.getElementById(targetSpanId).innerText = turnStatus.turnScore;

      scoreBtn.disabled = true;
      rollBtn.disabled = false;
      endTurnBtn.disabled = false;

      selectedElements.forEach((die) => die.remove());
    })
    .catch((error) => {
      console.error("Chyba: ", error);
      showMessage(error.message, true);
      scoreBtn.disabled = false;
    });
});

endTurnBtn.addEventListener("click", () => {
  rollBtn.disabled = true;
  scoreBtn.disabled = true;
  endTurnBtn.disabled = true;

  fetch("/api/dice/endTurn", { method: "POST" })
    .then((response) => {
      if (!response.ok) throw new Error("!response.ok in endTurn");
      return response.json();
    })
    .then((data) => {
      const isPlayer1Active = document
        .getElementById("player1-card")
        .classList.contains("active");
      const totalScoreSpanId = isPlayer1Active ? "score-p1" : "score-p2";

      document.getElementById(totalScoreSpanId).innerText = data.totalScore;
      document.getElementById("actual-score-p1").innerText = "0";
      document.getElementById("actual-score-p2").innerText = "0";

      if (data.isWinner === true) {
        if (typeof showMessage === "function") {
          showMessage("🎉 " + data.message + " 🎉", false);
        }

        diceArea.innerHTML = `<h2 class="winner-text">Konec hry! Vítězí ${isPlayer1Active ? "Hráč 1" : "Hráč 2"}</h2>`;
      } else {
        switchActivePlayerUI();
        rollBtn.disabled = false;
      }
    })
    .catch((error) => {
      console.error(error);
      showMessage(error.message, true);
      endTurnBtn.disabled = false;
    });
});
