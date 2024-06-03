import Post, { PostRequest } from "@/types/Post";
import User from "@/types/User";
import Comment, { CommentRequest } from "@/types/Comment";
import AuthManager from "@/utils/AuthManager";
import Role, { RoleBase } from "@/types/Role";
import role from "@/types/Role";
import Deletable from "@/types/Deletable";
import WithId from "@/types/WithId";

export default class ApiClient {
    private static _instance?: ApiClient;

    public static get instance() {
        if (!this._instance) {
            this._instance = new ApiClient();
        }
        return this._instance;
    }

    private readonly _posts: PostsClient;
    private readonly _users: UsersClient;
    private readonly _roles: RolesClient;

    private constructor() {
        this._posts = new PostsClient();
        this._users = new UsersClient();
        this._roles = new RolesClient();
    }

    public get posts(): PostsClient {
        return this._posts;
    }

    public get users(): UsersClient {
        return this._users;
    }

    public get roles(): RolesClient {
        return this._roles;
    }
}

class PostsClient implements ApiBase<Post> {

    public async getAll(page?: number) {
        let url = "/api/posts";
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
        return await response.json() as Post[];
    }

    public async get(id: number) {
        const response = await fetch(`/api/posts/${id}`, {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json"
            })
        });
        return await response.json() as Post;
    }

    public async totalPages() {
        const response = await fetch("/api/posts/pages", {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json"
            })
        });
        return await response.json() as number;
    }

    public async getComments(id: number) {
        const response = await fetch(`/api/posts/${id}/comments`, {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json"
            })
        });
        return await response.json() as Comment[];
    }

    public async postPost(post: PostRequest) {
        const response = await fetch("/api/posts", {
            method: "POST",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            }),
            body: JSON.stringify(post)
        });
        return await response.json() as Post;
    }

    public async postComment(comment: CommentRequest) {
        const response = await fetch("/api/comments", {
            method: "POST",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            }),
            body: JSON.stringify(comment)
        });
        return await response.json() as Comment;
    }

    public async updateComment(id: number, text: string) {
        const response = await fetch(`/api/comments/${id}`, {
            method: "PUT",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            }),
            body: text
        });
        return await response.json() as Comment;
    }

    public async like(id: number) {
        const response = await fetch(`/api/posts/${id}/likes`, {
            method: "POST",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            })
        });
        return await response.json() as LikesResponse;
    }

    public async hasLike(id: number) {
        const response = await fetch(`/api/posts/${id}/likes`, {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            })
        });
        return await response.json() as boolean;
    }

    public async delete(id: number) {
        return await fetch(`/api/posts/${id}`, {
            method: "DELETE",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            })
        });
    }

    public async update(newItem: Post) {
        const response = await fetch(`/api/posts/${newItem.id}`, {
            method: "PUT",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json",
                ["Authorization"]: `Bearer ${AuthManager.instance.token}`
            })
        });

        return await response.json() as Post;
    }
}

class UsersClient implements ApiBase<User> {

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

class RolesClient implements ApiBase<Role> {
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

interface ApiBase<T> {
    delete(id: number): Promise<Response>;

    totalPages(): Promise<number>;

    get(id: number): Promise<T>;

    getAll(page?: number): Promise<T[]>;

    update(newItem: T): Promise<T>;
}

type LikesResponse = {
    id: number,
    likes: number
}