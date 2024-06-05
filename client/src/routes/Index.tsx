import { createEffect, createMemo, createResource, createSignal, For, Match, Show, Switch } from "solid-js";
import PostComponent from "@components/PostComponent";
import Post from "@/types/Post";
import ApiClient from "@/utils/ApiClient";
import { Loading } from "@components/Loading";
import { A, useLocation, useSearchParams } from "@solidjs/router";
import { useAuthContext } from "@/utils/AuthContext";
import PaginationComponent from "@components/PaginationComponent";
import { createQuery } from "@tanstack/solid-query";
import Icon from "@components/Icon";

export default function Index() {
    const [user] = useAuthContext();
    const [searchParams, setSearchParams] = useSearchParams<PostSearchParams>();
    const pages = createQuery(() => ({
        queryKey: ["postsPages"],
        queryFn: async () => await ApiClient.instance.posts.totalPages(),
    }));
    const page = createMemo(v => Number(searchParams.page) ? Number(searchParams.page) : 1);

    const posts = createQuery(() => ({
        queryKey: ["posts", page()],
        queryFn: async () => await ApiClient.instance.posts.getAll(page())
    }));
    return (
        <div class="flex flex-col gap-4">
            <Show when={user()}>
                <A href="/posts" class="button max-sm:m-4">Новый пост</A>
            </Show>
            <Switch>
                <Match when={posts.isLoading}>
                    <Icon code={"\ue9d0"} size={"3rem"} class="animate-spin" />
                </Match>
                <Match when={posts.isFetched}>
                    <For each={posts.data}>{(item, index) =>
                        <PostComponent value={item} navigatable />
                    }</For>
                    <Show when={pages.isFetched && pages.data! > 1}>
                        <PaginationComponent totalPages={pages.data!} page={page()}
                                             onPageChange={page => setSearchParams({ page })} />
                    </Show>
                </Match>
            </Switch>
        </div>);
}

type PostSearchParams = {
    page?: string
}