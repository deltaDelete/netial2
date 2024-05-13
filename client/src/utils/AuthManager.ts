import { createSignal, Signal } from "solid-js";
import user from "@/types/User";

export default class AuthManager {
    private static _instance?: AuthManager;

    public static get instance() {
        if (!this._instance) {
            this._instance = new AuthManager();
        }
        return this._instance;
    }

    /**
     * Retrieves the token value from the local storage.
     *
     * @return {string | undefined} The token value stored in the local storage, or undefined if it is not found.
     */
    public get token() {
        return localStorage.getItem("token") ?? undefined;
    }

    /**
     * Sets the token value in the local storage.
     *
     * @param {string | undefined} token - The token value to be set in the local storage.
     * @return {void} This function does not return a value.
     */
    public set token(token: string | undefined) {
        if (!token) {
            localStorage.removeItem("token");
            return;
        }
        localStorage.setItem("token", token);
    }

    /**
     * Authorizes the user with the provided login credentials.
     *
     * @param {LoginBody} credentials - The login credentials of the user.
     * @return {Promise<boolean>} - A promise that resolves to true if the user is authorized successfully, false otherwise.
     */
    public async authorize(credentials: LoginBody) {
        const headers = new Headers();
        headers.set("Content-Type", "application/json");
        const response = await fetch("/api/login", {
            method: "POST",
            headers: headers,
            body: JSON.stringify(credentials)
        });
        if (response.status == 400) {
            return { result: AuthResult.BadCredentials, response: await response.json() };
        }
        if (!response.ok) {
            return { result: AuthResult.Error, response: await response.json() };
        }

        const token = await response.json() as TokenResponse | undefined;
        if (!token) {
            return { result: AuthResult.Error, response: token };
        }
        this.token = token.token;

        return { result: AuthResult.Success, response: token };
    }

    /**
     * Sets the token value in the local storage to undefined, effectively logging out the user.
     *
     * @return {void} This function does not return a value.
     */
    public logout() {
        this.token = undefined;
    }

    /**
     * Retrieves the user information from the token.
     *
     * @return {JWTPayload | undefined} The user information parsed from the token payload, or undefined if the token is not present.
     */
    public get user(): JWTPayload | undefined {
        if (!this.token) return undefined;
        const [_1, payload] = this.token.split(".");
        const jwt = JSON.parse(atob(payload)) as JWTPayload;
        return jwt;
    }

    async register(credentials: RegisterBody): Promise<{ result: AuthResult, response: any | number }> {
        const headers = new Headers();
        headers.set("Content-Type", "application/json");
        const response = await fetch("/api/register", {
            method: "POST",
            headers: headers,
            body: JSON.stringify(credentials)
        });
        if (response.status == 400) {
            return { result: AuthResult.BadCredentials, response: await response.json() };
        }
        if (response.status != 201) {
            return { result: AuthResult.Error, response: await response.json() };
        }

        const userId = await response.json() as number | undefined;

        return { result: AuthResult.Success, response: userId };
    }
}

export type LoginBody = {
    userName: string,
    password: string
}

export type RegisterBody = {
    lastName: string,
    firstName: string,
    birthDate: number,
    userName: string,
    email: string,
    password: string
}

export type JWTPayload = {
    sub: number,
    aud: string,
    iss: string,
    userName: string,
    iat: number,
    exp: number,
}

export enum AuthResult {
    Success,
    BadCredentials,
    Error
}

type TokenResponse = {
    token: string
}