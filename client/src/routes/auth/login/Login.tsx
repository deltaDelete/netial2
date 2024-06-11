import { createSignal, Show } from "solid-js";
import { AuthResult, LoginBody } from "@/utils/AuthManager";
import Input from "@components/InputComponent";
import { useAuthContext } from "@/utils/AuthContext";
import { useNavigate } from "@solidjs/router";
import { Button } from "@kobalte/core/button";
import Icon from "@components/Icon";

export default function Login() {
    const [user, {authorize, logout}] = useAuthContext()
    const navigate = useNavigate()
    const onSubmit = async (e: SubmitEvent) => {
        e.preventDefault();
        setPending(true);
        const loginBody = credentials();
        console.log(loginBody);
        authorize(loginBody).then(({ result, response }) => {
            setPending(false);
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

    const [pending, setPending] = createSignal(false);
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
                <Button class="button small" type="submit">
                    <Show when={pending()} fallback={"Войти"}>
                        <Icon code={"\ue9d0"} size={"1.2rem"} class="animate-spin" />
                    </Show>
                </Button>
            </form>
        </div>
    );
}
