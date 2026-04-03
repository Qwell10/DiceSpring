const diceArea = document.getElementById("diceArea");
const gameMessage = document.getElementById("gameMessage");
const rollBtn = document.getElementById("rollBtn");
const scoreBtn = document.getElementById("scoreBtn");
const endTurnBtn = document.getElementById('endTurnBtn');

function switchActivePlayerUI() {
  const player1Box = document.getElementById("player1-card");
  const player2Box = document.getElementById("player2-card");

  player1Box.classList.toggle("active");
  player2Box.classList.toggle("active");
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
  diceArea.innerHTML = "";

  fetch("/api/dice/roll", { method: "POST" })
    .then((response) => response.json())
    .then((data) => {
      const diceValues = data.dice;
      const diceElements = [];

      for (let i = 0; i < diceValues.length; i++) {
        const die = document.createElement("div");
        die.className = "die rolling";
        die.innerText = "?";
        diceArea.appendChild(die);
        diceElements.push(die);
      }

      setTimeout(() => {
        diceElements.forEach((die, index) => {
          die.classList.remove("rolling");
          die.innerText = diceValues[index];

          if (data.isBust === false) {
            die.addEventListener("click", () => {
              die.classList.toggle("selected");
              const selectedCount = document.querySelectorAll(".die.selected").length;

              if (selectedCount > 0) {
                scoreBtn.disabled = false; 
              } else {
                scoreBtn.disabled = true; 
              }
            });
          } else {
            die.classList.add("bust-die");
          }
        });

        if (data.isBust === true) {
          showMessage(data.message, true);

          document.getElementById('actual-score-p1').innerText = '0';
          document.getElementById('actual-score-p2').innerText = '0';

          setTimeout(() => {
            diceArea.innerHTML = "";
            switchActivePlayerUI();
            rollBtn.disabled = false; 
          }, 3000);
        } else {
           
        }
      }, 600);
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
          throw new Error(data.errorMessage || data.message || "Neznámá chyba serveru ???");
        }
        return data;
      });
    })
    .then((turnStatus) => {
      const isPlayer1Active = document.getElementById("player1-card").classList.contains("active");
      const targetSpanId = isPlayer1Active ? "actual-score-p1" : "actual-score-p2";
      
      document.getElementById(targetSpanId).innerText = turnStatus.turnScore;

      scoreBtn.disabled = true;   
      rollBtn.disabled = false;   
      endTurnBtn.disabled = false; 

      selectedElements.forEach(die => die.remove()); 
    })
    .catch((error) => {
      console.error("Chyba: ", error);
      showMessage(error.message, true); 
      scoreBtn.disabled = false; 
    });
});


endTurnBtn.addEventListener('click', () => {
  rollBtn.disabled = true;
  scoreBtn.disabled = true;
  endTurnBtn.disabled = true;

  fetch('/api/dice/endTurn', { method: 'POST' })
    .then(response => {
        if (!response.ok) throw new Error("!response.ok in endTurn");
        return response.json();
    })
    .then(data => {
        const isPlayer1Active = document.getElementById('player1-card').classList.contains('active');
        const totalScoreSpanId = isPlayer1Active ? 'score-p1' : 'score-p2';
        
        document.getElementById(totalScoreSpanId).innerText = data.totalScore;
        document.getElementById('actual-score-p1').innerText = '0';
        document.getElementById('actual-score-p2').innerText = '0';

        diceArea.innerHTML = '';
        switchActivePlayerUI();
        rollBtn.disabled = false;
    })
    .catch(error => {
        console.error(error);
        showMessage(error.message, true);
        endTurnBtn.disabled = false; 
    });
});