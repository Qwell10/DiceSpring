const diceArea = document.getElementById("diceArea");
const gameMessage = document.getElementById("gameMessage");
const rollBtn = document.getElementById("rollBtn");
const scoreBtn = document.getElementById("scoreBtn");
const endTurnBtn = document.getElementById("endTurnBtn");

let myPlayerId = 0;
let myRole = "";
let stompClient = null;
let currentActivePlayerId = 1;

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
  updateButtonsUI(1);

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
        updatePlayerStatusUI(
          status.isPlayer1Connected,
          status.isPlayer2Connected,
        );
      });

stompClient.subscribe("/topic/game-state", function (message) {
  const gameState = JSON.parse(message.body);
  console.log("Nový stav hry ze serveru:", gameState);

  if (currentActivePlayerId !== gameState.activePlayerId) {
    currentActivePlayerId = gameState.activePlayerId;
    updateButtonsUI(gameState.activePlayerId);

    setActivePlayerWindow(gameState.activePlayerId);
  }

  if (!gameState.diceOnTable || gameState.diceOnTable.length === 0) {
    diceArea.innerHTML = "";
  }

  if (gameState.player1) {
    document.getElementById("score-p1").innerText = gameState.player1.totalScore;
    document.getElementById("actual-score-p1").innerText = gameState.player1.turnScore;
  }
  if (gameState.player2) {
    document.getElementById("score-p2").innerText = gameState.player2.totalScore;
    document.getElementById("actual-score-p2").innerText = gameState.player2.turnScore;
  }

  if (myPlayerId !== gameState.activePlayerId) {
    if (gameState.diceOnTable && gameState.diceOnTable.length > 0) {
      renderDice(gameState.diceOnTable, false, false, gameState.isNewRoll);
    }
  }
});

      stompClient.subscribe("/topic/dice-selection", function (message) {
        const selectionData = JSON.parse(message.body);
        console.log("Změna výběru kostky ze serveru:", selectionData);

        const currentDiceElements = diceArea.children;

        if (currentDiceElements[selectionData.dieIndex]) {
          const targetDie = currentDiceElements[selectionData.dieIndex];

          if (selectionData.isSelected === true) {
            targetDie.classList.add("selected");
          } else {
            targetDie.classList.remove("selected");
          }
        }
      });

      fetch("/api/dice/status")
        .then((response) => response.json())
        .then((status) => {
          console.log("Načten úvodní stav:", status);
          updatePlayerStatusUI(
            status.isPlayer1Connected,
            status.isPlayer2Connected,
          );
        })
        .catch((error) =>
          console.error("❌ Chyba při načítání úvodního stavu:", error),f
        );
    },
    function (error) {
      console.error("❌ Chyba WebSocketu: " + error);
    },
  );
}

function setActivePlayerWindow(activeId) {
  const p1Card = document.getElementById("player1-card");
  const p2Card = document.getElementById("player2-card");

  if (activeId === 1) {
    p1Card.classList.add("active");
    p2Card.classList.remove("active");
  } else {
    p1Card.classList.remove("active");
    p2Card.classList.add("active");
  }
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


function renderDice(diceValues, isBust, allowSelection, animate = true) {
  diceArea.innerHTML = "";
  const diceElements = [];

  for (let i = 0; i < diceValues.length; i++) {
    const die = document.createElement("div");
    die.className = animate ? "die rolling" : "die";
    die.innerText = animate ? "?" : diceValues[i];
    diceArea.appendChild(die);
    diceElements.push(die);
  }

  const timeoutMs = animate ? 600 : 0;

  setTimeout(() => {
    diceElements.forEach((die, index) => {
      die.classList.remove("rolling");
      die.innerText = diceValues[index];

      if (isBust === true) {
        die.classList.add("bust-die");
      } 

      else if (allowSelection === true) {
        die.addEventListener("click", () => {
          
          const isNowSelected = die.classList.toggle("selected");
          
          const selectedCount = document.querySelectorAll(".die.selected").length;
          if (selectedCount > 0) {
            scoreBtn.disabled = false;
          } else {
            scoreBtn.disabled = true;
          }

          if (stompClient && stompClient.connected) {
             const messageObj = {
                 dieIndex: index,          
                 isSelected: isNowSelected 
             };
             
             stompClient.send("/app/game.select-die", {}, JSON.stringify(messageObj));
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

document.getElementById("create-room-btn").addEventListener("click", async () => {
    try {
        // Zde v budoucnu zavoláš svůj backend: fetch('/api/dice/create-room')
        // Pro teď si kód nasimulujeme, abychom viděli ten přechod:
        const simulatedRoomCode = "X9";

        // 1. Vepíšeme kód místnosti do připraveného místa v hlavičce
        document.getElementById("display-room-code").innerText = simulatedRoomCode;

        // 2. Schováme Lobby
        document.getElementById("lobby-screen").style.display = "none";

        // 3. Ukážeme herní stůl
        document.getElementById("game-screen").style.display = "flex";

        console.log("Stůl vytvořen, čekám na Hráče 2 s kódem:", simulatedRoomCode);

        // Tady pak v budoucnu spustíš připojení k WebSocketu: connect(simulatedRoomCode);

    } catch (error) {
        console.error("Chyba při vytváření místnosti:", error);
    }
});

// Tlačítko pro připojení k existující hře
document.getElementById("join-room-btn").addEventListener("click", async () => {
    // 1. Přečteme si, co hráč napsal do políčka.
    const inputCode = document.getElementById("room-code-input").value.trim().toUpperCase();

    // Pokud políčko nechal prázdné a klikl
    if (inputCode === "") {
        alert("Zadej kód místnosti!");
        return;
    }

    // 2. Tady se budeme v budoucnu ptát backendu, jestli kód platí.
    // Teď jen pro FE testování
    if (inputCode === "X9") {

        // Přechod - stejný jako u Hráče 1
        document.getElementById("display-room-code").innerText = inputCode;
        document.getElementById("lobby-screen").style.display = "none";
        document.getElementById("game-screen").style.display = "block";

        console.log("Úspěšně připojeno jako Hráč 2 ke stolu:", inputCode);

    } else {
        // Pokud zadá cokoliv jiného
        alert("Tato místnost neexistuje!");
    }
});

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
          
          fetch("/api/dice/endTurn", { method: "POST" })
            .catch(error => console.error("Chyba při automatickém ukončení tahu po Bustu:", error));
            
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
    .then((data) => {
      const isPlayer1Active = document
        .getElementById("player1-card")
        .classList.contains("active");
      const targetSpanId = isPlayer1Active
        ? "actual-score-p1"
        : "actual-score-p2";

      document.getElementById(targetSpanId).innerText = data.turnScore;

      scoreBtn.disabled = true;
      rollBtn.disabled = false;
      endTurnBtn.disabled = false;

     renderDice(data.diceOnTable, false, true, false)
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
      if (!response.ok) throw new Error("Chyba při ukončování tahu.");
      return response.json();
    })
    .then((data) => {
      if (data.isWinner === true) {
        if (typeof showMessage === "function") {
          showMessage("🎉 " + data.message + " 🎉", false);
        }

        diceArea.innerHTML = `<h2 class="winner-text">Konec hry! Vítězí Hráč ${myPlayerId}</h2>`;
      } 
      
    })
    .catch((error) => {
      console.error(error);
      showMessage(error.message, true);
      endTurnBtn.disabled = false;
    });
});
