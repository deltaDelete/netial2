import { Accessor, createContext, createSignal, JSX, Setter, Signal, useContext } from "solid-js";
import Post from "@/types/Post";
import Comment from "@/types/Comment";

const PostContext = createContext<PostContextValue>();

export function PostContextProvider(props: {
    children: number | boolean | Node | JSX.ArrayElement | (string & {}) | null | undefined,
    post: Post,
}) {
    const post = createSignal<Post>(props.post);
    const comments = createSignal<Comment[]>([]);

    const service: PostContextValue = [
        post,
        comments
    ];

    return (
        <PostContext.Provider value={service}>
            {props.children}
        </PostContext.Provider>
    );
}

export function usePostContext() {
    return useContext(PostContext)!!;
}

type PostContextValue = [
    Signal<Post>, Signal<Comment[]>
];