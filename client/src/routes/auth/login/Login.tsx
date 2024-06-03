import { TextField } from "@kobalte/core/text-field";
import { createEffect, createSignal, JSX, Show } from "solid-js";
import AuthManager, { AuthResult, LoginBody } from "@/utils/AuthManager";
import Input from "@components/InputComponent";
import { useAuthContext } from "@/utils/AuthContext";
import { useNavigate } from "@solidjs/router";

export default function Login() {
    const [user, {authorize, logout}] = useAuthContext()
    const navigate = useNavigate()
    const onSubmit = (e: SubmitEvent) => {
        e.preventDefault();
        const loginBody = credentials();
        console.log(loginBody);
        authorize(loginBody).then(({ result, response }) => {
            if (result == AuthResult.BadCredentials) {
                setHasHasError("invalid");
                response.message && setErrorMessage(response.message);
                return;
            }
            if (result == AuthResult.Error) {
                setHasHasError("invalid");
                response.message && setErrorMessage(response.message);
                return;
            }
            navigate("/");
        });
    };
    const [username, setUsename] = createSignal("");
    const [password, setPassword] = createSignal("");
    const [hasError, setHasHasError] = createSignal<"valid" | "invalid" | undefined>(undefined);
    const [errorMessage, setErrorMessage] = createSignal<undefined | string>(undefined);
    const credentials = (): LoginBody => {
        return {
            userName: username(),
            password: password()
        };
    };

    return (
        <div class="container root-container">
            <h1 class="text-2xl text-center heading uppercase">Вход</h1>
            <form onSubmit={onSubmit} class="gap-4 flex flex-col">
                <Input label="Логин" type={"text"} name="username" required onChange={setUsename} valid={hasError()} />
                <Input label="Пароль" type={"password"} name="password" required onChange={setPassword}
                       valid={hasError()} />
                <Show when={errorMessage()}>
                    <p class="error-message">{errorMessage()}</p>
                </Show>
                <button class="button small" type="submit">Войти</button>
            </form>
        </div>
    );
}
