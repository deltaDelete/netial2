import { Pagination } from "@kobalte/core/pagination";
import Icon from "@components/Icon";
import "./PaginationComponent.css";

export default function PaginationComponent(props: PaginationProps) {
    return (
        <Pagination
            count={props.totalPages}
            showFirst={props.showFirst}
            showLast={props.showLast}
            page={props.page}
            onPageChange={props.onPageChange}
            class="__pagination_root"
            itemComponent={props => <Pagination.Item page={props.page} class="__pagination_item">{props.page}</Pagination.Item>}
            ellipsisComponent={() => <Pagination.Ellipsis class="__pagination_ellipsis">...</Pagination.Ellipsis>}
        >
            <Pagination.Previous class="__pagination_item"><Icon code={"\ue5c4"} size="1.5rem"/></Pagination.Previous>
            <Pagination.Items />
            <Pagination.Next class="__pagination_item"><Icon code={"\ue5c8"} size="1.5rem"/></Pagination.Next>
        </Pagination>
    )
}

export type PaginationProps = {
    totalPages: number,
    showFirst?: boolean,
    showLast?: boolean,
    page?: number,
    onPageChange?: (page: number) => void
}