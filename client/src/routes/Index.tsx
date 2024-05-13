import { createResource, For, Show } from "solid-js";
import PostComponent from "@components/PostComponent";
import Post from "@/types/Post";
import ApiClient from "@/utils/ApiClient";
import { Loading } from "@components/Loading";
import { A } from "@solidjs/router";
import { useAuthContext } from "@/utils/AuthContext";

export default function Index() {
    const [user] = useAuthContext();

    const [posts] = createResource(async () => {
        return ApiClient.instance.posts.getPosts();
    });
    return (
        <div class="flex flex-col gap-4">
            <Show when={user()}>
                <A href="/posts" class="button">Новый пост</A>
            </Show>
            <Show when={!posts.loading} fallback={Loading()}>
                <For each={posts()}>{(item, index) =>
                    <PostComponent value={item} navigatable />
                }</For>
            </Show>
        </div>);
}
