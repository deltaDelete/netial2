import { JSX, createContext, useContext, Signal, Accessor, createSignal } from "solid-js";
import AuthManager, { AuthResult, JWTPayload, LoginBody, RegisterBody } from "@/utils/AuthManager";

const AuthContext = createContext<AuthContextValue>();

export function AuthContextProvider(props: {
    children: number | boolean | Node | JSX.ArrayElement | (string & {}) | null | undefined;
}) {
    const [user, setUser] = createSignal<JWTPayload | undefined>(AuthManager.instance.user);

    const service: AuthContextValue = [
        user,
        {
            async authorize(credentials: LoginBody) {
                const value = await AuthManager.instance.authorize(credentials);
                setUser(AuthManager.instance.user);
                return value;
            },
            logout() {
                AuthManager.instance.logout();
                setUser(AuthManager.instance.user);
            },
            async register(credentials: RegisterBody) {
                return await AuthManager.instance.register(credentials);
            }
        }
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
    }
];