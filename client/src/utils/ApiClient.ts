import { RolesClient } from "@/utils/RolesClient";
import { UsersClient } from "@/utils/UsersClient";
import { PostsClient } from "@/utils/PostsClient";
import { MessagesClient } from "@/utils/MessagesClient";

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
    private readonly _messages: MessagesClient;

    private constructor() {
        this._posts = new PostsClient();
        this._users = new UsersClient();
        this._roles = new RolesClient();
        this._messages = new MessagesClient();
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

    public get messages(): MessagesClient {
        return this._messages;
    }
}

