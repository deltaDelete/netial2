import { createEffect, createMemo, createSignal, JSX } from "solid-js";
import render from "solidjs-markdoc";
import Markdoc, { RenderableTreeNode } from "@markdoc/markdoc";
import attachment from "@/markdoc/schema/Attachment.markdoc";
import { Dynamic } from "solid-js/web";
import "./markdown.css";
import { BundledLanguage, codeToHtml } from "shiki";

export function Renderer(props: MarkdocRendererProps): JSX.Element {
    const ast = createMemo(() => Markdoc.parse(props.content));
    const config = {
        tags: {
            attachment
        }
    };
    const content = createMemo<RenderableTreeNode>(() => {
        const tree = Markdoc.transform(ast(), config) as MarkdocNode;
        if (tree && props.limit) {
            tree.children.length > 2 && (tree.children = tree.children.splice(0, props.limit));
        }
        return tree;
    });
    return render(content(), {
        components: {
            h1: (props: HTMLHeadingElement) => <Heading level={1} {...props} />,
            h2: (props: HTMLHeadingElement) => <Heading level={2} {...props} />,
            h3: (props: HTMLHeadingElement) => <Heading level={3} {...props} />,
            h4: (props: HTMLHeadingElement) => <Heading level={4} {...props} />,
            h5: (props: HTMLHeadingElement) => <Heading level={5} {...props} />,
            h6: (props: HTMLHeadingElement) => <Heading level={6} {...props} />,
            pre: Code,
            li: ListItem,
            article: Root,
            Attachment,
            "a": Link
        }
    });
}

export type MarkdocRendererProps = {
    content: string,
    limit?: number,
}

type MarkdocNode = {
    attributes: any,
    children: MarkdocNode[],
    name: string
}

export function Attachment(props: { src: string, label: any, size: string }): JSX.Element {
    return (
        <a href={props.src}
           rel="noreferrer noopener"
           target="_blank"
           class="attachment">
            <span>
                {props.label} {props.size}
            </span>
        </a>
    );
}

function Heading(props: HTMLHeadingElement & { level: number }): JSX.Element {
    return (
        <a href={`#${props.children}`}>
            <Dynamic component={`h${props.level}`} id={props.children} class="header">
                {props.children}
            </Dynamic>
        </a>
    );
}

function Code(props: { "data-language": BundledLanguage, children: string[] }): JSX.Element {
    const [ref, setRef] = createSignal<HTMLPreElement | undefined>();
    createEffect(async () => {
        const current = ref();
        if (!current) {
            return;
        }
        current.innerHTML = await codeToHtml(props.children[0], {
            lang: props["data-language"],
            theme: "github-dark"
        });
    });
    return <div ref={setRef} class="code" lang={props["data-language"] ?? undefined} />;
}

function ListItem(props: { children: any[] }) {
    const regex = /^\[([x ])?\] (.+)/;
    const result = regex.exec(props.children[0]);
    if (result) {
        props.children.shift();
        console.log(result);
        return (
            <li classList={{
                checked: result[1] == "x",
                unchecked: result[1] == " "
            }}>
                {result[2]}
                {props.children}
            </li>
        );
    }
    return (
        <li>{props.children}</li>
    );
}

function Link(props: any) {
    return (
        <a {...props} class="link" rel="noopener noreferrer" target="_blank" />
    );
}

function Root(props: { children: any[] }) {
    return (<article class="markdown">
        {props.children}
    </article>);
}

export { Renderer as default };