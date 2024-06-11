import { ApiBase } from "@/utils/ApiBase";
import Role from "@/types/Role";
import role, { RoleBase } from "@/types/Role";
import AuthManager from "@/utils/AuthManager";
import WithId from "@/types/WithId";

export class RolesClient implements ApiBase<Role> {
    public async getAll(page?: number) {
        let url = "/api/roles";
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
        return await response.json() as Role[];
    }

    public async get(id: number) {
        const response = await fetch(`/api/roles/${id}`, {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json"
            })
        });
        return await response.json() as role;
    }

    public async totalPages() {
        const response = await fetch("/api/roles/pages", {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json"
            })
        });
        return await response.json() as number;
    }

    public async delete(id: number) {
        return await fetch(`/api/roles/${id}`, {
            method: "DELETE",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            })
        });
    }

    public async update(newItem: RoleBase & WithId) {
        const response = await fetch(`/api/roles/${newItem.id}`, {
            method: "PUT",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            }),
            body: JSON.stringify(newItem as RoleBase)
        });

        return await response.json() as Role;
    }
}