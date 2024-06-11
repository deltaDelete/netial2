import { ApiBase } from "@/utils/ApiBase";
import Post, { PostRequest } from "@/types/Post";
import Comment, { CommentRequest } from "@/types/Comment";
import AuthManager from "@/utils/AuthManager";
import { LikesResponse } from "@/utils/LikesResponse";

export class PostsClient implements ApiBase<Post> {

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