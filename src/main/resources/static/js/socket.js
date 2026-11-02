import { state } from './state.js';
import { updatePlayerStatusUI, updateButtonsUI, setActivePlayerWindow, renderDice, diceArea } from './ui.js';

export function connect() {
    console.log("🔌 [Fáze 3: connect()] Funkce connect byla zavolána. Vytvářím SockJS...");

    const socket = new SockJS("/ws");
    state.stompClient = Stomp.over(socket);

    const headers = {
        playerId: state.myPlayerId,
        roomCode: state.currentRoomCode
    };

    state.stompClient.connect(
        headers,
        function (frame) {
            console.log("✅ WebSocket připojen: " + frame);

            state.stompClient.subscribe("/topic/player-status/" + state.currentRoomCode, function (statusMessage) {
                const status = JSON.parse(statusMessage.body);
                updatePlayerStatusUI(
                    status.isPlayer1Connected,
                    status.isPlayer2Connected
                );
            });

            state.stompClient.subscribe("/topic/game-state/" + state.currentRoomCode, function (message) {
                const gameState = JSON.parse(message.body);
                console.log("Nový stav hry ze serveru:", gameState);

                if (state.currentActivePlayerId !== gameState.activePlayerId) {
                    state.currentActivePlayerId = gameState.activePlayerId;
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

                if (state.myPlayerId !== gameState.activePlayerId) {
                    if (gameState.diceOnTable && gameState.diceOnTable.length > 0) {
                        renderDice(gameState.diceOnTable, false, false, gameState.isNewRoll);
                    }
                }
            });

            state.stompClient.subscribe("/topic/dice-selection/" + state.currentRoomCode, function (message) {
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

            state.stompClient.send("/app/status/request/" + state.currentRoomCode, {}, "");

        },
        function (error) {
            console.error("❌ Chyba WebSocketu: " + error);
        }
    );
}