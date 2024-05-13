import Post, { PostRequest } from "@/types/Post";
import User from "@/types/User";
import Comment, { CommentRequest } from "@/types/Comment";
import AuthManager from "@/utils/AuthManager";

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

    private constructor() {
        this._posts = new PostsClient();
        this._users = new UsersClient();
    }

    public get posts(): PostsClient {
        return this._posts;
    }

    public get users(): UsersClient {
        return this._users;
    }
}

class PostsClient {

    public async getPosts() {
        const response = await fetch("/api/posts", {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json"
            })
        });
        return await response.json() as Post[];
    }

    public async getPost(id: number) {
        const response = await fetch(`/api/posts/${id}`, {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json"
            })
        });
        return await response.json() as Post;
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
}

class UsersClient {

    public async getUsers() {
        const response = await fetch("/api/users", {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json"
            })
        });
        return await response.json() as User[];
    }

    public async getUser(id: number) {
        const response = await fetch(`/api/users/${id}`, {
            method: "GET",
            headers: new Headers({
                ["Content-Type"]: "application/json",
                ["Accept"]: "application/json"
            })
        });
        return await response.json() as User;
    }
}