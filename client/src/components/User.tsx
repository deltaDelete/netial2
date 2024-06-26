import User from "@/types/User";
import { createResource, Show } from "solid-js";
import ApiClient from "@/utils/ApiClient";
import { DropdownMenu } from "@kobalte/core/dropdown-menu";
import AuthManager from "@/utils/AuthManager";
import { useAuthContext } from "@/utils/AuthContext";
import { A } from "@solidjs/router";

export function AccountComponent() {
    const [user, {authorize, logout}] = useAuthContext()!;
    const id = () => user()?.sub;
    console.log(id());
    const [userRemote] = createResource<User, number>(id, async (k: number) => {
        return await ApiClient.instance.users.get(k);
    });
    return (
        <Show when={userRemote()}>
            <DropdownMenu>
                <DropdownMenu.Trigger>
                    <div class="flex flex-row gap-1 place-items-center">
                        <div class="text-xl button ghost small">{userRemote()!.firstName} {userRemote()!.lastName}</div>
                    </div>
                </DropdownMenu.Trigger>
                <DropdownMenu.Portal>
                    <DropdownMenu.Content class="dropdown">
                        <DropdownMenu.Item class="button ghost">
                            <A href={`/profile/${userRemote()!.id}`}>Профиль</A>
                        </DropdownMenu.Item>
                        <DropdownMenu.Item class="button ghost" onClick={() => logout()}>
                            <A href={"/"}>Выйти</A>
                        </DropdownMenu.Item>
                    </DropdownMenu.Content>
                </DropdownMenu.Portal>
            </DropdownMenu>
        </Show>
    );
}

export default function UserComponent(props: UserComponentProps) {
    const id = () => props.id;
    const [user] = createResource<User, number>(id, async (k: number) => {
        return await ApiClient.instance.users.get(k);
    }, {
        initialValue: props.user,
        ssrLoadFrom: "initial"
    });
    const classes = () => props.class?.split(" ").concat("text-xl text-inherit button ghost small".split(" ")).join(" ")
    return (
        <Show when={user()}>
            <div class="flex gap-1 place-items-center text-inherit" classList={{
                "text": props.reverse,
                "text-start": props.reverse,
                "flex-row": !props.reverse
            }}>
                <A state={{ user: user() }} href={`/users/${user()!.id}`} class={classes()}>{user()!.firstName} {user()!.lastName}</A>
            </div>
        </Show>
    );
}

type UserComponentProps = {
    id?: number,
    user?: User,
    reverse?: true,
    class?: string
}