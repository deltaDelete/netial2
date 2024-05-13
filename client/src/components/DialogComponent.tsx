import { JSX, Show } from "solid-js";
import "./DialogComponent.css";
import { Dialog, DialogRootProps } from "@kobalte/core/dialog";

export default function DialogComponent(props: DialogComponentProps) {
    return (
        <Dialog open={props.open} modal={props.modal}>
            <Dialog.Trigger disabled={props.disabled} class={props.triggerClass} onClick={() => props.onOpenChange?.call(undefined, true)}>
                {props.trigger}
            </Dialog.Trigger>
            <Dialog.Portal>
                <Dialog.Overlay class="dialog__overlay" onClick={() => props.onOpenChange?.call(undefined, false)} />
                <div class="dialog__positioner">
                    <Dialog.Content class="dialog__content container">
                        <div class="dialog__header">
                            <Dialog.Title class="dialog__title">{props.title}</Dialog.Title>
                            <Dialog.CloseButton class="dialog__close button ghost"
                                                onClick={() => props.onOpenChange?.call(undefined, false)}>
                                X
                            </Dialog.CloseButton>
                        </div>
                        <Show when={props.description}>
                            <Dialog.Description class="dialog__description">
                                {props.description}
                            </Dialog.Description>
                        </Show>
                        {props.children}
                    </Dialog.Content>
                </div>
            </Dialog.Portal>
        </Dialog>
    );
}

type DialogComponentProps = DialogRootProps & {
    trigger: number | boolean | Node | JSX.ArrayElement | (string & {}) | null | undefined,
    title: string,
    description?: string,
    disabled?: boolean,
    triggerClass?: string,
    children: number | boolean | Node | JSX.ArrayElement | (string & {}) | null | undefined
}