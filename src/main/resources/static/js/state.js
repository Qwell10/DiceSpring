export const state = {
    myPlayerId: sessionStorage.getItem("playerId") || null,
    currentRoomCode: sessionStorage.getItem("roomCode") || null,
    myRole: "",
    stompClient: null,
    currentActivePlayerId: null
};