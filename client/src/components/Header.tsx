import logo from "../logo.svg";
import AuthManager from "@/utils/AuthManager";
import { Show } from "solid-js";
import UserComponent, { AccountComponent } from "@components/User";
import { DropdownMenu } from "@kobalte/core/dropdown-menu";
import { useAuthContext } from "@/utils/AuthContext";

export default function Header() {
    const [user, {authorize, logout}] = useAuthContext()!;
    return (
        <header>
            <a href="/" class="gap-1 flex flex-row items-center text-xl button ghost small">
                <img src={logo} alt="logo" class="logo" />
                <span>etial</span>
            </a>
            <div>

            </div>
            <Show when={user()} fallback={(
                <div class="gap-1 flex flex-row">
                    <a href="/auth/login" class="button">Войти</a>
                    <a href="/auth/register" class="button secondary">Зарегистрироваться</a>
                </div>
            )}>
                <AccountComponent />
            </Show>
        </header>
    );
}