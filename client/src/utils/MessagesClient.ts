import { ApiBase } from "@/utils/ApiBase";
import { Message } from "@/types/Message";
import Role, { RoleBase } from "@/types/Role";
import role from "@/types/Role";
import AuthManager from "@/utils/AuthManager";

export class MessagesClient {
    async delete(id: number): Promise<Response> {
        return await fetch(`/api/messages/${id}`, {
            method: "DELETE",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            })
        });
    }

    async get(id: number): Promise<Message> {
        const response = await fetch(`/api/messages/${id}`, {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            })
        });
        return await response.json() as Message;

    }

    async getAll(
        userIdTo?: number,
        groupIdTo?: number,
        page?: number | undefined
    ): Promise<Message[]> {
        let url = "/api/messages";
        const params = new URLSearchParams({
            page: "1"
        });
        if (page) {
            params.set("page", page.toString());
        }
        if (userIdTo) {
            params.set("userTo", userIdTo.toString());
        }
        if (groupIdTo) {
            params.set("groupTo", groupIdTo.toString());
        }
        url = url + "?" + params;
        const response = await fetch(url, {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            })
        });
        return await response.json() as Message[];
    }

    async update(newItem: Message): Promise<Message> {
        const response = await fetch(`/api/messages/${newItem.id}`, {
            method: "PUT",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            }),
            body: newItem.text
        });

        return await response.json() as Message;
    }

}