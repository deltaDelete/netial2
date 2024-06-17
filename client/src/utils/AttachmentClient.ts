import { ApiBase } from "@/utils/ApiBase";
import { Attachment, AttachmentBase } from "@/types/Attachment";
import AuthManager from "@/utils/AuthManager";

export class AttachmentClient implements ApiBase<Attachment> {
    async delete(id: number): Promise<Response> {
        return await fetch(`/api/attachments/${id}`, {
            method: "DELETE",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            })
        });
    }

    async totalPages(): Promise<number> {
        const response = await fetch("/api/attachments/pages", {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json"
            })
        });
        return await response.json() as number;
    }

    async get(id: number): Promise<Attachment> {
        const response = await fetch(`/api/attachments/${id}`, {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json"
            })
        });
        return await response.json() as Attachment;
    }

    async getAll(page?: number | undefined): Promise<Attachment[]> {
        let url = "/api/attachments";
        if (page) {
            const params = new URLSearchParams({
                page: page.toString()
            });
            url = url + "?" + params;
        }
        const response = await fetch(url, {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json"
            })
        });
        return await response.json() as Attachment[];
    }

    async update(newItem: Attachment): Promise<Attachment> {
        const response = await fetch(`/api/attachments/${newItem.id}`, {
            method: "PUT",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            }),
            body: JSON.stringify(newItem)
        });

        return await response.json() as Attachment;
    }

    async upload(id:number, file: File): Promise<any> {
        return await fetch(`/api/attachments/${id}/data`, {
            method: "POST",
            headers: new Headers({
                ["Accept"]: "application/json",
                ["Content-Type"]: "application/octet-stream",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            }),
            body: file
        });
    }

    async post(item: AttachmentBase): Promise<Attachment> {
        const response = await fetch(`/api/attachments`, {
            method: "POST",
            headers: new Headers({
                ["Accept"]: "application/json",
                ["Content-Type"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            }),
            body: JSON.stringify(item)
        });

        return await response.json() as Attachment;
    }
}