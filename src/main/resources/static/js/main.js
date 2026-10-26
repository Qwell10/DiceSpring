import { state } from './state.js';
import { createRoomAction, joinRoomAction } from './api.js';
import { connect } from './socket.js';
import { rollBtn, scoreBtn, endTurnBtn, diceArea, showMessage, renderDice } from './ui.js';

document.addEventListener("DOMContentLoaded", async () => {
    const savedRoomCode = sessionStorage.getItem("roomCode");
    const savedPlayerId = sessionStorage.getItem("playerId");

    if (savedRoomCode && savedPlayerId) {
        try {
            const response = await fetch(`/api/dice/join-room/${savedRoomCode}?playerId=${savedPlayerId}`, {
                method: "POST"
            });

            if (response.ok) {
                state.currentRoomCode = savedRoomCode;
                state.myPlayerId = savedPlayerId;

                document.getElementById("lobby-screen").style.display = "none";
                document.getElementById("game-screen").style.display = "flex";
                document.getElementById("display-room-code").innerText = state.currentRoomCode;

                connect();
            } else {
                sessionStorage.removeItem("roomCode");
                sessionStorage.removeItem("playerId");
            }
        } catch (error) {
            console.error("❌ Došlo k chybě při volání REST API:", error);
        }
    }
});

window.addEventListener("beforeunload", () => {
    if (state.stompClient !== null) {
        state.stompClient.disconnect(() => {});
    }
});

document.getElementById("create-room-btn").addEventListener("click", createRoomAction);
document.getElementById("join-room-btn").addEventListener("click", joinRoomAction);

rollBtn.addEventListener("click", () => {
    rollBtn.disabled = true;
    scoreBtn.disabled = true;
    endTurnBtn.disabled = true;

    fetch(`/api/dice/${state.currentRoomCode}/roll`, { method: "POST" })
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
                    fetch(`/api/dice/${state.currentRoomCode}/endTurn`, { method: "POST" })
                        .catch(error => console.error("Chyba při ukončení po Bustu:", error));
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

    scoreBtn.disabled = true;

    fetch(`/api/dice/${state.currentRoomCode}/score`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(selectedDice),
    })
        .then((response) => {
            return response.json().then((data) => {
                if (!response.ok) throw new Error(data.errorMessage || data.message || "Chyba serveru");
                return data;
            });
        })
        .then((data) => {
            const isPlayer1Active = document.getElementById("player1-card").classList.contains("active");
            const targetSpanId = isPlayer1Active ? "actual-score-p1" : "actual-score-p2";

            document.getElementById(targetSpanId).innerText = data.turnScore;

            scoreBtn.disabled = true;
            rollBtn.disabled = false;
            endTurnBtn.disabled = false;

            renderDice(data.diceOnTable, false, true, false);
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

    fetch(`/api/dice/${state.currentRoomCode}/endTurn`, { method: "POST" })
        .then((response) => {
            if (!response.ok) throw new Error("Chyba při ukončování tahu.");
            return response.json();
        })
        .then((data) => {
            if (data.isWinner === true) {
                if (typeof showMessage === "function") showMessage("🎉 " + data.message + " 🎉", false);
                diceArea.innerHTML = `<h2 class="winner-text">Konec hry! Vítězí Hráč ${state.myPlayerId}</h2>`;
            }
        })
        .catch((error) => {
            console.error(error);
            showMessage(error.message, true);
            endTurnBtn.disabled = false;
        });
});