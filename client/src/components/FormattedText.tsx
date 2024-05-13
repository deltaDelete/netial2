import { For } from "solid-js";

export default function FormattedText(props: { text: string }) {
    const text = () => props.text.split("\n");
    return (
        <>
            <For each={text()}>{(item, index) => (
                <>
                    {item}
                    <br/>
                </>
            )}</For>
        </>
    );
}