import { JSX, createContext, useContext, Signal, Accessor, createSignal } from "solid-js";
import AuthManager, { AuthResult, JWTPayload, LoginBody, RegisterBody } from "@/utils/AuthManager";
import role from "@/types/Role";
import ApiClient from "@/utils/ApiClient";
import { Permission } from "@/types/Permission";

const AuthContext = createContext<AuthContextValue>();

export function AuthContextProvider(props: {
    children: number | boolean | Node | JSX.ArrayElement | (string & {}) | null | undefined;
}) {
    const [user, setUser] = createSignal<JWTPayload | undefined>(AuthManager.instance.user);
    const [roles, setRoles] = createSignal<role[]>(AuthManager.instance.roles);

    const service: AuthContextValue = [
        user,
        {
            async authorize(credentials: LoginBody) {
                const value = await AuthManager.instance.authorize(credentials);
                setUser(AuthManager.instance.user);
                setRoles(AuthManager.instance.roles);
                return value;
            },
            logout() {
                AuthManager.instance.logout();
                setUser(AuthManager.instance.user);
                setRoles([]);
            },
            async register(credentials: RegisterBody) {
                return await AuthManager.instance.register(credentials);
            },
            hasPermission(permission: Permission) {
                return () => roles().some(value => (value.permissions & permission) == permission);
            }
        },
        roles
    ];

    return (
        <AuthContext.Provider value={service}>
            {props.children}
        </AuthContext.Provider>
    );
}

export function useAuthContext() {
    return useContext(AuthContext)!!;
}

type AuthContextValue = [
    Accessor<JWTPayload | undefined>,
    {
        authorize: (credentials: LoginBody) => Promise<{result: AuthResult, response: any}>,
        register: (credentials: RegisterBody) => Promise<{result: AuthResult, response: any | number}>,
        logout: () => void,
        hasPermission: (permission: Permission) => () => boolean,
    },
    Accessor<role[]>
];