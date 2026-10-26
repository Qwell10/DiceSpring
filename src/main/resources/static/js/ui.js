import { state } from './state.js';

export const diceArea = document.getElementById("diceArea");
export const gameMessage = document.getElementById("gameMessage");
export const rollBtn = document.getElementById("rollBtn");
export const scoreBtn = document.getElementById("scoreBtn");
export const endTurnBtn = document.getElementById("endTurnBtn");

export function setActivePlayerWindow(activeId) {
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

export function updatePlayerStatusUI(p1Connected, p2Connected) {
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

export function updateButtonsUI(activePlayerId) {
    if (state.myPlayerId === activePlayerId) {
        rollBtn.disabled = false;
        scoreBtn.disabled = true;
        endTurnBtn.disabled = true;
    } else {
        rollBtn.disabled = true;
        scoreBtn.disabled = true;
        endTurnBtn.disabled = true;
    }
}

export function renderDice(diceValues, isBust, allowSelection, animate = true) {
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
            } else if (allowSelection === true) {
                die.addEventListener("click", () => {
                    const isNowSelected = die.classList.toggle("selected");

                    const selectedCount = document.querySelectorAll(".die.selected").length;
                    if (selectedCount > 0) {
                        scoreBtn.disabled = false;
                    } else {
                        scoreBtn.disabled = true;
                    }

                    if (state.stompClient && state.stompClient.connected) {
                        const messageObj = {
                            dieIndex: index,
                            isSelected: isNowSelected
                        };
                        state.stompClient.send("/app/game.select-die", {}, JSON.stringify(messageObj));
                    }
                });
            }
        });
    }, 600);
}

export function showMessage(text, isError) {
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