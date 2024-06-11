import Post, { PostRequest } from "@/types/Post";
import UserComponent from "@components/User";
import "./PostComponent.css";
import "./DialogComponent.css";
import FormattedText from "@components/FormattedText";
import { useNavigate } from "@solidjs/router";
import { useAuthContext } from "@/utils/AuthContext";
import DialogComponent from "@components/DialogComponent";
import { createSignal, JSX } from "solid-js";
import ApiClient from "@/utils/ApiClient";
import Input from "@components/InputComponent";
import { PostContextProvider, usePostContext } from "@/utils/PostContext";
import { Comment, CommentRequest } from "@/types/Comment";
import { Button } from "@kobalte/core/button";
import Markdoc from "@/markdoc/Markdoc";
import { createResource } from "solid-js";
import { createInfiniteQuery, createQuery, useQueryClient } from "@tanstack/solid-query";

export default function PostComponent(props: PostComponentProps) {
    const navigate = useNavigate();
    const [user] = useAuthContext();

    const [dialogOpen, setDialogOpen] = createSignal<boolean>(false);

    return (
        <PostContextProvider post={props.value}>
            <div class="post self-stretch">
                <div class="post-text"
                     onClick={() => props.navigatable && navigate(`/posts/${props.value.id}`, "target" in props.value ? { state: { post: props.value.target } } : undefined)}
                     classList={{
                         "cursor-pointer": props.navigatable
                     }}
                >
                    <Markdoc content={props.value.text} limit={props.navigatable && 2} />
                </div>
                <div class="text-end flex flex-row justify-between content-center text-white/50">
                    <UserComponent user={props.value.user} reverse />
                    <span class="self-center">{new Date(props.value.creationDate * 1000).toLocaleString()}</span>
                </div>
                <div class="flex flex-row gap-1">
                    <div class="button-group">
                        <LikeButton />
                        <DialogComponent
                            trigger={<>
                                Комментарии
                                <div class="counter">{props.value.comments}</div>
                            </>
                            }
                            triggerClass="button secondary small grow"
                            title="Новый комментарий"
                            disabled={!user()}
                            open={dialogOpen()}
                            onOpenChange={setDialogOpen}>
                            <NewComment onClose={() => setDialogOpen(false)} />
                        </DialogComponent>
                        <Button disabled={!user()} class="button secondary small grow">Поделиться</Button>
                    </div>
                </div>
            </div>
            {props.children}
        </PostContextProvider>
    );
}

type PostComponentProps = {
    value: Post,
    navigatable?: true,
    children?: any
}

function NewComment(props: { onClose: () => void }) {
    const [user] = useAuthContext();
    const [[post], [comments, setComments]] = usePostContext();

    const [text, setText] = createSignal("");
    const [isValid, setIsValid] = createSignal<"valid" | "invalid">("valid");

    const comment = (): CommentRequest => {
        return {
            text: text(),
            postId: post().id!!,
            userId: Number(user()?.sub)
        };
    };

    const onSubmit = (e: SubmitEvent) => {
        e.preventDefault();
        const commentRequest = comment();
        if (commentRequest.text == "") return setIsValid("invalid");
        ApiClient.instance.posts.postComment(commentRequest).then(result => {
            if (result && result satisfies Comment) {
                setComments([...comments(), result]);
                props.onClose();
            }
        });
    };

    return (
        <form onSubmit={onSubmit} class="flex flex-col gap-4">
            <Input name="text" multiline onChange={setText} valid={isValid()} required label="Текст комментария" />
            <button class="button" type="submit">Опубликовать</button>
        </form>
    );
}

function LikeButton() {
    const [user] = useAuthContext();
    const [[post, setPost], [comments, setComments]] = usePostContext();
    const [likes, setLikes] = createSignal(post().likes);

    const onLikeClick = async () => {
        const currentPost = post();
        if (!currentPost.id) {
            return;
        }
        const likesResponse = await ApiClient.instance.posts.like(currentPost.id);
        if (likesResponse.id != currentPost.id) {
            return;
        }
        setLikes(likesResponse.likes);
        hasLike.data = true;
    };

    const hasLike = createQuery(() => ({
        queryKey: ["hasLike", post().id],
        queryFn: async () => await ApiClient.instance.posts.hasLike(post().id!!),
        enabled: !!post().id && !!user()
    }));


    return (
        <Button disabled={!user()} class="button small grow" classList={{
            "primary": hasLike.data,
            "secondary": !hasLike.data
        }} onClick={onLikeClick}>
            Нравится
            <div class="counter">{likes()}</div>
        </Button>
    );
}