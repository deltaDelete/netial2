import { ApiBase } from "@/utils/ApiBase";
import User from "@/types/User";
import role from "@/types/Role";
import AuthManager from "@/utils/AuthManager";

export class UsersClient implements ApiBase<User> {

    public async getAll(page?: number) {
        let url = "/api/users";
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
        return await response.json() as User[];
    }

    public async totalPages() {
        const response = await fetch("/api/users/pages", {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json"
            })
        });
        return await response.json() as number;
    }

    public async get(id: number) {
        const response = await fetch(`/api/users/${id}`, {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json"
            })
        });
        return await response.json() as User;
    }

    public async getRoles(id: number) {
        const response = await fetch(`/api/users/${id}/roles`, {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json"
            })
        });
        return await response.json() as role[];
    }

    public async delete(id: number) {
        return await fetch(`/api/users/${id}`, {
            method: "DELETE",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            })
        });
    }

    public async update(newItem: User) {
        const response = await fetch(`/api/users/${newItem.id}`, {
            method: "PUT",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            })
        });

        return await response.json() as User;
    }
}