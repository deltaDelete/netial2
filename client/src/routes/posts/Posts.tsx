import { createResource, For, Show } from "solid-js";
import ApiClient from "@/utils/ApiClient";
import { Loading } from "@components/Loading";
import PostComponent from "@components/PostComponent";
import { useLocation, useParams } from "@solidjs/router";
import Post from "@/types/Post";
import CommentComponent from "@components/CommentComponent";
import { usePostContext } from "@/utils/PostContext";

export default function Posts() {
    const params = useParams<{ id: string }>();
    const id = () => Number(params.id);
    const location = useLocation<{ post: Post }>();

    const [post] = createResource(id, async (k: number) => {
        return await ApiClient.instance.posts.getPost(k);
    }, {
        ssrLoadFrom: "initial",
        initialValue: location.state?.post
    });

    return (
        <div class="flex flex-col gap-4">
            <Show when={!post.loading} fallback={Loading()}>
                <PostComponent value={post()!}>
                    <PostComments />
                </PostComponent>
            </Show>
        </div>);
}

function PostComments() {
    const [[post], [getComments, setComments]] = usePostContext();
    const id = () => post().id;
    const [comments] = createResource(id, async (k: number) => {
        const comments = await ApiClient.instance.posts.getComments(k);
        setComments(comments);
        return comments;
    }, {
        initialValue: getComments()
    });
    return (
        <Show when={!comments.loading} fallback={Loading()}>
            <For each={getComments()}>{(item, index) =>
                <CommentComponent value={item} />
            }</For>
        </Show>
    );
}