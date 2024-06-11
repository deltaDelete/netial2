import { useAuthContext } from "@/utils/AuthContext";
import { AuthResult, LoginBody, RegisterBody } from "@/utils/AuthManager";
import { createSignal, Show } from "solid-js";
import Input from "@components/InputComponent";
import { useNavigate } from "@solidjs/router";
import Icon from "@components/Icon";
import { Button } from "@kobalte/core/button";

const emailRegEx: RegExp = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;

export default function Register() {
    const [user, { authorize, logout, register }] = useAuthContext();
    const navigate = useNavigate();

    const onSubmit = (e: SubmitEvent) => {
        e.preventDefault();
        setPending(true);
        const registerBody = credentials();
        console.log(registerBody);
        register(registerBody).then(({ result, response }) => {
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
            navigate("/auth/register/confirm", { state: { userId: response as number } });
        });
    };

    const [pending, setPending] = createSignal(false);
    const [username, setUsename] = createSignal("");
    const [password, setPassword] = createSignal("");
    const [email, setEmail] = createSignal("");
    const [firstName, setFirstName] = createSignal("");
    const [lastName, setLastName] = createSignal("");
    const [birthDate, setBirthDate] = createSignal("");

    const [hasError, setHasHasError] = createSignal<"valid" | "invalid" | undefined>(undefined);
    const [errorMessage, setErrorMessage] = createSignal<undefined | string>(undefined);
    const [emailError, setEmailError] = createSignal<undefined | string>(undefined)

    const isEmailValid = () => {
        const current = email();
        if (!current || !emailRegEx.test(current)) {
            setEmailError("Неверный формат электронной почты");
            return "invalid";
        }
        return undefined;
    }

    const credentials = (): RegisterBody => {
        return {
            userName: username(),
            password: password(),
            email: email(),
            birthDate: new Date(birthDate()).getTime() / 1000,
            firstName: firstName(),
            lastName: lastName()
        };
    };

    return (
        <div class="container root-container">
            <h1 class="text-2xl text-center heading uppercase">Регистрация</h1>
            <form onSubmit={onSubmit} class="gap-4 flex flex-col">
                <Input label="Логин" type={"text"} name="username" required onChange={setUsename} valid={hasError()} />
                <Input label="Пароль" type={"password"} name="password" required onChange={setPassword}
                       valid={hasError()} />
                <Input label="Email" type={"email"} name="email" required onChange={setEmail} valid={isEmailValid() || hasError()} error={emailError()} />
                <Input label="Имя" type={"text"} name="firstName" required onChange={setFirstName} valid={hasError()} />
                <Input label="Фамилия" type={"text"} name="lastName" required onChange={setLastName} valid={hasError()} />
                <Input label="Дата рождения" type={"date"} name="birthDate" required onChange={setBirthDate} valid={hasError()} />
                <Show when={errorMessage()}>
                    <p class="error-message">{errorMessage()}</p>
                </Show>
                <Button class="button small" type="submit">
                    <Show when={pending()} fallback={"Зарегистрироваться"}>
                        <Icon code={"\ue9d0"} size={"1.2rem"} class="animate-spin" />
                    </Show>
                </Button>
            </form>
        </div>
    );
}