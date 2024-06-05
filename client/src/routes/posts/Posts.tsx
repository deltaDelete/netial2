import { createResource, createSignal, For, mapArray, Show } from "solid-js";
import ApiClient from "@/utils/ApiClient";
import { Loading } from "@components/Loading";
import PostComponent from "@components/PostComponent";
import { useLocation, useParams } from "@solidjs/router";
import Post from "@/types/Post";
import CommentComponent from "@components/CommentComponent";
import { usePostContext } from "@/utils/PostContext";
import { createQuery } from "@tanstack/solid-query";
import Icon from "@components/Icon";

export default function Posts() {
    const params = useParams<{ id: string }>();
    const id = () => Number(params.id);
    const location = useLocation<{ post: Post }>();

    const post = createQuery(() => ({
        queryKey: ["post", id()],
        queryFn: async () => {
            return await ApiClient.instance.posts.get(id());
        },
        initialData: () => location.state?.post
    }));

    return (
        <div class="flex flex-col gap-4 items-center">
            <Show when={post.isFetched} fallback={<Icon code={"\ue9d0"} size={"3rem"} class="animate-spin" />}>
                <PostComponent value={post.data!}>
                    <PostComments />
                </PostComponent>
            </Show>
        </div>);
}

function PostComments() {
    const [[post], [getComments, setComments]] = usePostContext();
    const id = () => post().id;
    const comments = createQuery(() => ({
        queryKey: ["postComments", id()],
        queryFn: async () => {
            const comments = await ApiClient.instance.posts.getComments(id()!);
            setComments(comments);
            return comments;
        },
        initialData: () => getComments().length > 0 ? getComments() : undefined
    }))
    return (
        <Show when={comments.isFetched} fallback={<Icon code={"\ue9d0"} size={"3rem"} class="animate-spin" />}>
            <For each={comments.data}>{(item, index) =>
                <CommentComponent value={item} />
            }</For>
        </Show>
    );
}