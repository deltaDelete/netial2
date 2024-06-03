import { TextField } from "@kobalte/core/text-field";
import { Match, Show, Switch } from "solid-js";
import "./InputComponent.css";

function Input(props: InputComponentProps) {
    return (
        <TextField class="text-input" required={props.required} name={props.name} onChange={props.onChange}
                   validationState={props.valid}>
            <Show when={props.label}>
                <TextField.Label class="text-input-label">{props.label}</TextField.Label>
            </Show>
            <Switch>
                <Match when={props.multiline}>
                    <TextField.TextArea class="text-input-input" autoResize={true} value={props.value}/>
                </Match>
                <Match when={!props.multiline}>
                    <TextField.Input type={props.type} class="text-input-input" value={props.value} />
                </Match>
            </Switch>
            <Show when={props.error}>
                <TextField.ErrorMessage class="text-input-error">{props.error}</TextField.ErrorMessage>
            </Show>
        </TextField>
    );
}

export type InputComponentProps = {
    label?: string,
    type?: string,
    required?: true,
    onChange?: ((value: string) => void),
    valid?: "valid" | "invalid",
    error?: string,
    multiline?: boolean,
    name: string,
    value?: string | string[] | number,
}

export default Input;