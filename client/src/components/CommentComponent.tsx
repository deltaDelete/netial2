import Comment from "@/types/Comment";
import UserComponent from "@components/User";
import FormattedText from "@components/FormattedText";
import "./CommentComponent.css";
import { DropdownMenu } from "@kobalte/core/dropdown-menu";
import { useAuthContext } from "@/utils/AuthContext";
import { Show } from "solid-js";

export default function CommentComponent(props: CommentComponentProps) {
// TODO show remove when user is admin or owner
    const [user] = useAuthContext();

    return (
        <div class="comment">
            <div class="text-end flex flex-row justify-between content-start text-white/50">
                <UserComponent user={props.value.user} reverse />
                <div class="flex flex-row justify-end gap-2">
                    <span
                        class="self-center justify-self-end">{new Date(props.value.creationDate * 1000).toLocaleString()}</span>
                    <Show when={user()?.sub == props.value.user.id}>
                        <DropdownMenu>
                            <DropdownMenu.Trigger>
                                <button class="button ghost small">...</button>
                            </DropdownMenu.Trigger>
                            <DropdownMenu.Content class="dropdown">
                                <DropdownMenu.Item class="button ghost small">
                                    Удалить
                                </DropdownMenu.Item>
                            </DropdownMenu.Content>
                        </DropdownMenu>
                    </Show>
                </div>
            </div>
            <div class="comment-text">
                <FormattedText text={props.value.text} />
            </div>
        </div>
    );
}


type CommentComponentProps = {
    value: Comment
}