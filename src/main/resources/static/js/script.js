const diceArea = document.getElementById("diceArea");
const gameMessage = document.getElementById("gameMessage");
const rollBtn = document.getElementById("rollBtn");
const scoreBtn = document.getElementById("scoreBtn");

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
              const selectedCount =
                document.querySelectorAll(".die.selected").length;
              scoreBtn.disabled = selectedCount === 0;
            });
          } else {
            die.classList.add("bust-die");
          }
        });

        if (data.isBust === true) {
          showMessage(data.message, true);

          setTimeout(() => {
            diceArea.innerHTML = "";
            switchActivePlayerUI();
            rollBtn.disabled = false;
          }, 3000);
        } else {
          rollBtn.disabled = false;
        }
      }, 600);
    })
    .catch((error) => {
      console.error("Chyba spojení s Javou:", error);
      showMessage(error.message, true);
      rollBtn.disabled = false;
    });

  scoreBtn.addEventListener("click", () => {
    const selectedElements = document.querySelectorAll(".die.selected");
    const selectedDice = [];

    selectedElements.forEach((die) => {
      const dieValue = parseInt(die.innerText);
      selectedDice.push(dieValue);
    });

    console.log("Selected dice to send:", selectedDice);

    scoreBtn.disabled = true; //hrac nemuze 2x za sebou rychle kliknout na tlacitko

    fetch("/api/dice/score", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(selectedDice),
    })
      .then((response) => {
        return response.json().then((data) => {
          if (!response.ok) {
            throw new Error(data.errorMessage);
          }
          return data;
        });
      })
      .then((turnStatus) => {
        console.log("Odpověď z Javy:", turnStatus);

        // tady bude 200 ok
        // pozdeji kod na smazani kostek
      })
      .catch((error) => {
        console.error("Chyba: ", error);
        showMessage(error.message, true); //vyskoci na hrace zprava o chybe

        scoreBtn.disabled = false; // kdyz se neco pokazi, tlacitko zase odemkneme
      });
  });
});
