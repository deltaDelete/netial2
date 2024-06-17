import Input from "@components/InputComponent";
import ApiClient from "@/utils/ApiClient";
import { PostRequest } from "@/types/Post";
import { useAuthContext } from "@/utils/AuthContext";
import { createSignal, For, Match, Show, Switch } from "solid-js";
import { A, useNavigate } from "@solidjs/router";
import { Tabs } from "@kobalte/core/tabs";
import Markdoc from "@/markdoc/Markdoc";
import Icon from "@components/Icon";
import AuthManager from "@/utils/AuthManager";
import { Attachment } from "@/types/Attachment";
import { createStore } from "solid-js/store";

export default function NewPost() {
    const [user] = useAuthContext();
    const navigate = useNavigate();

    const [text, setText] = createSignal("");
    const [isValid, setIsValid] = createSignal<"valid" | "invalid">("valid");
    const [drop, setDrop] = createSignal(false);
    const [attachments, setAttachments] = createSignal<Attachment[]>([]);
    const [uploading, setUploading] = createSignal<boolean>(false);

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

    const onDrop = (e: DragEvent) => {
        e.preventDefault();
        if (!e.dataTransfer) {
            setDrop(false);
            return;
        }
        if (e.dataTransfer.items) {
            const items = e.dataTransfer.items as DataTransferItemList;
            // Use DataTransferItemList interface to access the file(s)
            for (const item of items) {
                if (item.kind === "file") {
                    const file = item.getAsFile();
                    file && uploadFile(file);
                }
            }
        } else {
            // Use DataTransfer interface to access the file(s)
            const files = e.dataTransfer.files as FileList;
            for (const file of files) {
                file && uploadFile(file);
            }
        }
        setDrop(false);
    };

    const uploadFile = async (file: File) => {
        console.log(file);
        const attachment = await ApiClient.instance.attachments.post({
            hash: "",
            userId: AuthManager.instance.user?.sub!!,
            mimeType: file.type,
            name: file.name,
            size: file.size
        });
        setUploading(true);
        setAttachments([...attachments(), attachment]);
        await ApiClient.instance.attachments.upload(attachment.id!, file);
        setUploading(false);
        setText(prev => {
            return prev + `\n{% attachment src="/api/attachments/${attachment.id}/data" label="${attachment.name}" size="${attachment.size} байт" %}{% /attachment %}\n`;
        });
    };

    return (
        <div class="flex flex-col">
            <form onSubmit={onSubmit}
                  onDrop={onDrop}
                  onDragEnter={(e: any) => {
                      e.preventDefault();
                      setDrop(true);
                  }}
                  class="relative"
                  onDragExit={(e: any) => {
                      e.preventDefault();
                      setDrop(false);
                  }}>
                <Tabs class="flex flex-col gap-4 container root-container">
                    <Tabs.List>
                        <Tabs.Trigger value="text">Текст</Tabs.Trigger>
                        <Tabs.Trigger value="preview">Предпросмотр</Tabs.Trigger>
                    </Tabs.List>
                    <Tabs.Content value="text" class="flex flex-col gap-4">
                        <Input name="text" multiline onChange={setText} valid={isValid()} required
                               value={text()}
                               label="Текст поста" />
                        <p>
                            {"* Поддерживается "}
                            <a href="https://guides.hexlet.io/ru/markdown/" class="link" rel="noopener noreferrer"
                               target="_blank">Markdown</a>
                            {" и "}
                            <a href="https://markdoc.dev/" class="link" rel="noopener noreferrer"
                               target="_blank">Markdoc</a>
                        </p>
                        <For each={attachments()}>{item =>
                            <div class="attachment-all">
                                <Switch>
                                    <a href={`/api/attachments/${item.id}/data`}
                                       rel="noreferrer noopener"
                                       target="_blank"
                                       class="attachment">
                                        <Match when={uploading()}>
                                            <Icon code={"\ue9d0"} size={"1rem"} class="animate-spin" />
                                        </Match>
                                        <Match when={!uploading()}>
                                            <span>
                                                {item.name} {item.size}
                                            </span>
                                        </Match>
                                    </a>
                                </Switch>
                            </div>
                        }</For>
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
                <div classList={{
                    "drop": drop()
                }} class="dropzone">
                    <p>Прикрепить картинку</p>
                    <p>Прикрепить файл</p>
                </div>
            </form>
        </div>
    );
}