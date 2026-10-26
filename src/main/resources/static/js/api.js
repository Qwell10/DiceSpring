import { state } from './state.js';
import { connect } from './socket.js';
import { updatePlayerStatusUI } from './ui.js';

export async function createRoomAction() {
    try {
        const response = await fetch("/api/dice/create-room", { method: "POST" });
        if (!response.ok) throw new Error("Nepodařilo se vytvořit místnost");

        const data = await response.json();
        state.myPlayerId = data.playerId;
        state.currentRoomCode = data.roomCode;
        state.myRole = "Hráč 1";

        sessionStorage.setItem("playerId", state.myPlayerId);
        sessionStorage.setItem("roomCode", state.currentRoomCode);

        document.getElementById("display-room-code").innerText = state.currentRoomCode;
        document.getElementById("lobby-screen").style.display = "none";
        document.getElementById("game-screen").style.display = "flex";

        updatePlayerStatusUI(true, false);
        connect();
    } catch (error) {
        console.error("Chyba při vytváření místnosti:", error);
    }
}

export async function joinRoomAction() {
    const inputCode = document.getElementById("room-code-input").value.trim().toUpperCase();
    if (inputCode === "") {
        alert("Zadej kód místnosti!");
        return;
    }

    try {
        let tempPlayerId = sessionStorage.getItem("playerId");
        const playerIdQuery = tempPlayerId ? `?playerId=${tempPlayerId}` : "";

        const response = await fetch(`/api/dice/join-room/${inputCode}${playerIdQuery}`, {
            method: "POST"
        });

        if (!response.ok) {
            const errorText = await response.text();
            alert("Chyba: " + errorText);
            return;
        }

        const data = await response.json();
        state.myPlayerId = data.playerId;
        state.currentRoomCode = data.roomCode;
        state.myRole = "Hráč 2";

        sessionStorage.setItem("playerId", state.myPlayerId);
        sessionStorage.setItem("roomCode", state.currentRoomCode);

        document.getElementById("display-room-code").innerText = state.currentRoomCode;
        document.getElementById("lobby-screen").style.display = "none";
        document.getElementById("game-screen").style.display = "flex";

        connect();
    } catch (error) {
        console.error("Kritická chyba při připojování:", error);
    }
}