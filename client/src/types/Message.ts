import WithId from "@/types/WithId";
import Deletable from "@/types/Deletable";

export type Message = WithId & Deletable & MessageBase

export type MessageBase = {
    text: string,
    userId: number,
    userToId?: number,
    groupToId?: number,
    replyToId?: number,
}

export type WebSocketMessage = {
    type: string
}

export type MessageWS = Message & WebSocketMessage & {
    type: "message"
}

export type SystemMessage = WebSocketMessage & {
    type: "system",
    text: string
}

export type WebSocketAuth = WebSocketMessage & {
    type: "auth",
    token: string
}