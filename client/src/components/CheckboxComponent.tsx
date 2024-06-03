import { Checkbox, CheckboxRootOptions } from "@kobalte/core/checkbox";
import "./CheckboxComponent.css";
import Icon from "@components/Icon";
import { createSignal, Match, Show, splitProps, Switch } from "solid-js";

export default function CheckboxComponent(props: CheckboxComponentProps) {
    const [other, local] = splitProps(props, ["label", "description", "id"]);
    return (
        <Checkbox class="checkbox" {...local}>
            <Checkbox.Input class="checkbox__input" id={other.id} />
            <Checkbox.Control class="checkbox__control">
                <Checkbox.Indicator class="checkbox__indicator">
                    <Icon code={"\ue5ca"} size={"1.5rem"} />
                </Checkbox.Indicator>
            </Checkbox.Control>
            <Show when={other.label}>
                <Checkbox.Label class="checkbox__label">{other.label}</Checkbox.Label>
            </Show>
            <Show when={other.description}>
                <Checkbox.Label class="checkbox__description">{other.description}</Checkbox.Label>
            </Show>
        </Checkbox>
    );
}

type CheckboxComponentProps = CheckboxRootOptions & {
    label?: string,
    description?: string,
    id?: string
}