import Input from "@components/InputComponent";
import ApiClient from "@/utils/ApiClient";
import { PostRequest } from "@/types/Post";
import { useAuthContext } from "@/utils/AuthContext";
import { createSignal, Show } from "solid-js";
import { A, useNavigate } from "@solidjs/router";
import { Tabs } from "@kobalte/core/tabs";
import Markdoc from "@/markdoc/Markdoc";
import Icon from "@components/Icon";

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
            <form onSubmit={onSubmit}>
                <Tabs class="flex flex-col gap-4 container root-container">
                    <Tabs.List>
                        <Tabs.Trigger value="text">Текст</Tabs.Trigger>
                        <Tabs.Trigger value="preview">Предпросмотр</Tabs.Trigger>
                    </Tabs.List>
                    <Tabs.Content value="text" class="flex flex-col gap-4">
                        <Input name="text" multiline onChange={setText} valid={isValid()} required
                               value={text()}
                               label="Текст поста" />
                    </Tabs.Content>
                    <Tabs.Content value="preview" class="flex flex-col gap-4">
                        <Show when={text()} fallback={<p><Icon code={"\ue88e"} /> Содержимое пусто</p>}>
                            <div class="p-4 bg-background border border-white border-opacity-20 rounded-xl">
                                <Markdoc content={text()} />
                            </div>
                        </Show>
                    </Tabs.Content>
                    <button class="button" type="submit">Опубликовать</button>
                </Tabs>
            </form>
        </div>
    );
}