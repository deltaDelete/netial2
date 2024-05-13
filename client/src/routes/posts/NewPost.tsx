import Input from "@components/InputComponent";
import ApiClient from "@/utils/ApiClient";
import { PostRequest } from "@/types/Post";
import { useAuthContext } from "@/utils/AuthContext";
import { createSignal } from "solid-js";
import { A, useNavigate } from "@solidjs/router";

export default function NewPost() {
    const [user] = useAuthContext();
    const navigate = useNavigate();

    const [text, setText] = createSignal("");
    const [isValid, setIsValid] = createSignal<"valid" | "invalid">("valid");

    const post = (): PostRequest => {
        return {
            text: text(),
            isArticle: false,
            userId: Number(user()?.sub)
        };
    };

    const onSubmit = (e: SubmitEvent) => {
        e.preventDefault();
        const postRequest = post();
        if (postRequest.text == "") return setIsValid("invalid");
        ApiClient.instance.posts.postPost(postRequest)
            .then(result =>
                navigate(`/posts/${result.id}`, { state: { post: result } })
            );
    };

    return (
        <div class="flex flex-col">
            <form onSubmit={onSubmit} class="flex flex-col gap-4 container">
                <Input name="text" multiline onChange={setText} valid={isValid()} required label="Текст поста" />
                <button class="button" type="submit">Опубликовать</button>
            </form>
        </div>
    );
}