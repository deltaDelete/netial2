import logo from "../logo.svg";
import AuthManager from "@/utils/AuthManager";
import { For, Show } from "solid-js";
import UserComponent, { AccountComponent } from "@components/User";
import { DropdownMenu } from "@kobalte/core/dropdown-menu";
import { useAuthContext } from "@/utils/AuthContext";
import { A } from "@solidjs/router";
import Icon from "@components/Icon";
import { Navigation } from "@components/Navigation";

export default function Header() {
    const [user, { authorize, logout }] = useAuthContext()!;
    return (
        <header>
            <div class="gap-1 flex flex-row items-center basis-auto">
                <DropdownMenu>
                    <DropdownMenu.Trigger class="button ghost small p-8 lg:hidden">
                        <Icon code={"\ue5d2"} size={"1.5rem"} />
                    </DropdownMenu.Trigger>
                    <DropdownMenu.Content class="dropdown">
                        <For each={Navigation()}>{i =>
                            <DropdownMenu.Item>
                                <A {...i} />
                            </DropdownMenu.Item>
                        }
                        </For>
                    </DropdownMenu.Content>
                </DropdownMenu>
                <a href="/" class="gap-1 flex flex-row items-center text-xl button ghost small">
                    <img src={logo} alt="logo" class="logo" />
                    <span>etial</span>
                </a>
            </div>
            <div class="basis-1">

            </div>
            <Show when={user()} fallback={(
                <div class="gap-1 flex flex-row me-2 basis-auto">
                    <DropdownMenu>
                        <DropdownMenu.Trigger class="button ghost small p-8">
                            Вход
                        </DropdownMenu.Trigger>
                        <DropdownMenu.Content class="dropdown">
                            <DropdownMenu.Item>
                                <A href="/auth/login"
                                   class="button small">Войти</A>
                            </DropdownMenu.Item>
                            <DropdownMenu.Item>
                                <A href="/auth/register"
                                   class="button secondary small">Зарегистрироваться</A>
                            </DropdownMenu.Item>
                        </DropdownMenu.Content>
                    </DropdownMenu>
                </div>
            )}>
                <AccountComponent />
            </Show>
        </header>
    );
}