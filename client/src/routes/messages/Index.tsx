import {
    Show,
    createEffect,
    createSignal,
    onMount,
    For,
    createResource,
    onCleanup,
    Suspense,
    Switch,
    Match
} from "solid-js";
import WithId from "@/types/WithId";
import Deletable from "@/types/Deletable";
import { Message, MessageBase, MessageWS, SystemMessage, WebSocketAuth, WebSocketMessage } from "@/types/Message";
import AuthManager from "@/utils/AuthManager";
import { useAuthContext } from "@/utils/AuthContext";
import ApiClient from "@/utils/ApiClient";
import UserComponent from "@components/User";
import Input from "@components/InputComponent";
import Icon from "@components/Icon";
import { Button } from "@kobalte/core/button";
import MessageComponent from "@components/MessageComponent";
import { useSearchParams } from "@solidjs/router";
import PaginationComponent from "@components/PaginationComponent";
import User from "@/types/User";
import { createQuery } from "@tanstack/solid-query";
import { Loading } from "@components/Loading";

export default function Index() {
    const [open, setOpen] = createSignal(false);
    const [error, setError] = createSignal<string>();

    const [searchParams, setSearchParams] = useSearchParams<{
        user: string
    }>();
    const [messages, setMessages] = createSignal<Message[]>([]);
    const userTo = () => Number(searchParams.user);

    const ws = new WebSocket(`/api/ws`);
    onMount(() => {
        ws.addEventListener("open", ev => {
            console.log("Open WebSocket session");
            ws.send(JSON.stringify({
                type: "auth",
                token: AuthManager.instance.token
            } as WebSocketAuth));
            setOpen(true);
        });
        ws.addEventListener("error", ev => {
            setError(JSON.stringify(ev));
        });
        ws.addEventListener("message", ev => {
            const message = JSON.parse(ev.data);
            if (message satisfies WebSocketMessage && message.type == "message") {
                if (message.userId == userTo() || message.userToId == userTo()) {
                    setMessages([...messages(), JSON.parse(ev.data)]);
                }
                return;
            }
        });
        ws.addEventListener("close", ev => {
            console.log("Closing WebSocket session", ev);
            setOpen(false);
            setError("Отключен");
        });
    });
    onCleanup(() => {
        ws.close();
    });

    const [messageText, setMessageText] = createSignal<string>("");
    const onSubmit = async (event: { preventDefault: () => void; }) => {
        event.preventDefault();
        if (!messageText()) {
            return;
        }
        ws.send(JSON.stringify({
            type: "message",
            text: messageText(),
            userId: Number(AuthManager.instance.user?.sub),
            userToId: userTo()
        } as MessageWS));
        setMessageText("");
    };

    const pages = createQuery(() => ({
        queryKey: ["usersPages"],
        queryFn: async () => await ApiClient.instance.users.totalPages()
    }));
    const [page, setPage] = createSignal(1);
    const users = createQuery(() => ({
        queryKey: ["users", page()],
        queryFn: async () => {
            return await ApiClient.instance.users.getAll(page());
        }
    }));

    return (
        <div class="flex flex-col gap-4 justify-items-center w-full container root-container min-h-[80dvh]">
            <Show when={!userTo()}>
                <Switch>
                    <Match when={users.isLoading}>
                        <Icon code={"\ue9d0"} size={"3rem"} class="animate-spin" />
                    </Match>
                    <Match when={users.isFetched}>
                        <div class="inner-container flex flex-col gap-2 justify-items-center">
                            <For each={users.data}>{(item, index) => (
                                <div class="button ghost" onClick={() => setSearchParams({ user: item.id })}>
                                    {item.lastName} {item.firstName}
                                </div>
                            )}</For>
                            <Show when={pages.isFetched && pages.data! > 1}>
                                <PaginationComponent totalPages={pages.data!} page={page()} onPageChange={setPage} />
                            </Show>
                        </div>
                    </Match>
                </Switch>
            </Show>
            <Show when={userTo()}>
                <Button class="button ghost small gap-2 self-start"
                        onClick={() => setSearchParams({ user: undefined })}>
                    <Icon code={"\ue5c4"} size="1.5rem" />
                    Назад
                </Button>
                <div class="flex flex-col gap-2 justify-end overflow-y-scroll grow basis-1 inner-container">
                    <For each={messages()}>{item =>
                        <MessageComponent data={item} />
                    }</For>
                </div>
                <Show when={error()}>
                    <p class="bg-error p-2 rounded-md">{error()}</p>
                </Show>
                <form onSubmit={onSubmit} class="flex flex-row gap-2">
                    <Input name="text" class="text-input grow basis-1" disabled={!open()} value={messageText()}
                           onChange={setMessageText} />
                    <Button type="submit" class="button small"
                            disabled={!open() || !messageText()}>
                        <Icon code={"\ue163"} size="1.5rem" />
                    </Button>
                </form>
            </Show>
        </div>
    );
}
