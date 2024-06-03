import { createResource, createSignal, For, mapArray, Show } from "solid-js";
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
        return await ApiClient.instance.posts.get(k);
    }, {
        ssrLoadFrom: "initial",
        initialValue: location.state?.post
    });

    return (
        <div class="flex flex-col gap-4 items-center">
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
    const commentsArray = mapArray(comments, (value) => {
        const [text, setText] = createSignal(value.text);
        return {
            id: value.id,
            user: value.user,
            creationDate: value.creationDate,
            isDeleted: value.isDeleted,
            deletionDate: value.deletionDate,
            postId: value.postId,
            get text() {
                return text();
            },
            setText
        };
    });
    return (
        <Show when={!comments.loading} fallback={Loading()}>
            <For each={commentsArray()}>{(item, index) =>
                <CommentComponent value={item} />
            }</For>
        </Show>
    );
}