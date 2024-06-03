import { createEffect, createMemo, createResource, createSignal, For, Show } from "solid-js";
import PostComponent from "@components/PostComponent";
import Post from "@/types/Post";
import ApiClient from "@/utils/ApiClient";
import { Loading } from "@components/Loading";
import { A, useLocation, useSearchParams } from "@solidjs/router";
import { useAuthContext } from "@/utils/AuthContext";
import PaginationComponent from "@components/PaginationComponent";

export default function Index() {
    const [user] = useAuthContext();
    const [searchParams, setSearchParams] = useSearchParams<PostSearchParams>();
    const [pages] = createResource(async () => {
        return await ApiClient.instance.posts.totalPages();
    }, { initialValue: 1 });
    // const [page, setPage] = createSignal(Number(searchParams.page) ? Number(searchParams.page) : 1);
    const page = createMemo(v => Number(searchParams.page) ? Number(searchParams.page) : 1);

    const [posts] = createResource(page, async (source) => {
        return ApiClient.instance.posts.getAll(source);
    });
    return (
        <div class="flex flex-col gap-4">
            <Show when={user()}>
                <A href="/posts" class="button max-sm:m-4">Новый пост</A>
            </Show>
            <Show when={!posts.loading} fallback={Loading()}>
                <For each={posts()}>{(item, index) =>
                    <PostComponent value={item} navigatable />
                }</For>
                <Show when={!pages.loading && pages() > 1}>
                    <PaginationComponent totalPages={pages()} page={page()} onPageChange={page => setSearchParams({page})} />
                </Show>
            </Show>
        </div>);
}

type PostSearchParams = {
    page?: string
}