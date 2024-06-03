import Comment, { CommentRequest } from "@/types/Comment";
import UserComponent from "@components/User";
import FormattedText from "@components/FormattedText";
import "./CommentComponent.css";
import { DropdownMenu } from "@kobalte/core/dropdown-menu";
import { useAuthContext } from "@/utils/AuthContext";
import { createSignal, Setter, Show } from "solid-js";
import Icon from "@components/Icon";
import { usePostContext } from "@/utils/PostContext";
import ApiClient from "@/utils/ApiClient";
import Input from "@components/InputComponent";
import DialogComponent from "@components/DialogComponent";
import { tr } from "@markdoc/markdoc/dist/src/schema";
import User from "@/types/User";

export default function CommentComponent(props: CommentComponentProps) {
// TODO show remove when user is admin or owner
    const [user] = useAuthContext();
    const [dialogOpen, setDialogOpen] = createSignal(false);

    return (
        <div class="comment">
            <div class="text-end flex flex-row justify-between content-start text-white/50">
                <UserComponent user={props.value.user} reverse />
                <div class="flex flex-row justify-end gap-2">
                    <span
                        class="self-center justify-self-end">{new Date(props.value.creationDate * 1000).toLocaleString()}</span>
                    <Show when={user()?.sub == props.value.user.id}>
                        <DropdownMenu>
                            <DropdownMenu.Trigger class="button ghost small">
                                <Icon code={"\ue5d4"} size="1rem" />
                            </DropdownMenu.Trigger>
                            <DropdownMenu.Content class="dropdown">
                                <DropdownMenu.Item class="button ghost small">
                                    Удалить
                                </DropdownMenu.Item>
                                <DropdownMenu.Item class="button ghost small" onClick={() => setDialogOpen(true)}>
                                    Изменить
                                </DropdownMenu.Item>
                            </DropdownMenu.Content>
                        </DropdownMenu>

                        <DialogComponent triggerClass="hidden"
                                         title="Редактирование"
                                         disabled={!user()}
                                         open={dialogOpen()}
                                         onOpenChange={setDialogOpen}>
                            <ChangeComment comment={props.value}
                                           onClose={() => setDialogOpen(false)}
                                           setComment={value => props.value.setText(value.text)} />
                        </DialogComponent>
                    </Show>
                </div>
            </div>
            <p class="comment-text">
                <FormattedText text={props.value.text} />
            </p>
        </div>
    );
}

function ChangeComment(props: { onClose: () => void, comment: Comment, setComment: (value: Comment) => void }) {
    const [text, setText] = createSignal(props.comment.text);
    const [isValid, setIsValid] = createSignal<"valid" | "invalid">("valid");

    const onSubmit = (e: SubmitEvent) => {
        e.preventDefault();
        if (text() == "") return setIsValid("invalid");
        ApiClient.instance.posts.updateComment(props.comment.id!, text()).then(result => {
            if (result && result satisfies Comment) {
                props.setComment(result);
                props.onClose();
            }
        });
    };

    return (
        <form onSubmit={onSubmit} class="flex flex-col gap-4">
            <Input name="text" multiline onChange={setText} value={text()} valid={isValid()} required label="Текст комментария" />
            <button class="button" type="submit">Опубликовать</button>
        </form>
    );
}


type CommentComponentProps = {
    value: {
        id: number | undefined
        user: User
        creationDate: number
        isDeleted: boolean
        deletionDate: number | undefined
        postId: string
        readonly text: string
        setText: Setter<string>
    },
}