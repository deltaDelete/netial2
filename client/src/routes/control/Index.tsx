import { Tabs } from "@kobalte/core/tabs";
import { createResource, createSignal, For, Match, Show, Switch } from "solid-js";
import ApiClient from "@/utils/ApiClient";
import role from "@/types/Role";
import { Button } from "@kobalte/core/button";
import user from "@/types/User";
import "./control.css";
import DialogComponent from "@components/DialogComponent";
import PaginationComponent from "@components/PaginationComponent";
import { useAuthContext } from "@/utils/AuthContext";
import { Permission } from "@/types/Permission";
import { A, useLocation, useNavigate, useSearchParams } from "@solidjs/router";
import post from "@/types/Post";
import { useRouter } from "@solidjs/router/dist/routing";
import { Fallback } from "@kobalte/core/image";
import { createQuery, useQueryClient } from "@tanstack/solid-query";
import Icon from "@components/Icon";

export default function Index() {
    const [searchParams, setSearchParams] = useSearchParams();
    const [user, {}, roles] = useAuthContext();
    if (!user()) {
        return <h1>404</h1>;
    }
    return (
        <Tabs class="flex flex-col gap-4 self-stretch items-stretch" defaultValue={searchParams.tab}
              onChange={(value) => setSearchParams({ tab: value })}>
            <Tabs.List class="flex max-sm:self-stretch self-center p-4 basis-full grow">
                <Tabs.Trigger value="users">Пользователи</Tabs.Trigger>
                <Tabs.Trigger value="roles">Роли</Tabs.Trigger>
                <Tabs.Trigger value="posts">Публикации</Tabs.Trigger>
            </Tabs.List>
            <Tabs.Content value="users" class="flex flex-col items-center">
                <div class="container root-container">
                    <Users />
                </div>
            </Tabs.Content>
            <Tabs.Content value="roles" class="flex flex-col items-center">
                <div class="container root-container">
                    <Roles />
                </div>
            </Tabs.Content>
            <Tabs.Content value="posts" class="flex flex-col items-center">
                <div class="container root-container">
                    <Posts />
                </div>
            </Tabs.Content>
        </Tabs>
    );
}

function Users() {
    const queryClient = useQueryClient();
    const pages = createQuery(() => ({
        queryKey: ["usersPages"],
        queryFn: async () => await ApiClient.instance.users.totalPages()
    }));
    const [page, setPage] = createSignal(1);
    const users = createQuery(() => ({
        queryKey: ["users", page()],
        queryFn: async () => {
            return await ApiClient.instance.users.getAll(page());
        }
    }));
    return (
        <div class="flex flex-col gap-4">
            <Switch>
                <Match when={users.isLoading}>
                    <Icon code={"\ue9d0"} size={"3rem"} class="animate-spin" />
                </Match>
                <Match when={users.isFetched}>
                    <For each={users.data}>{(item, index) => (
                        <User user={item}
                              onDelete={() => item.id && ApiClient.instance.users.delete(item.id).then(value => {
                                  value.status == 204 && queryClient.invalidateQueries({
                                      queryKey: ["users", page()]
                                  });
                              })} />
                    )}</For>
                    <Show when={pages.isFetched && (pages.data! > 1)}>
                        <PaginationComponent totalPages={pages.data!} page={page()} onPageChange={setPage} />
                    </Show>
                </Match>
            </Switch>
        </div>
    );
}

function User(props: { user: user, onDelete: (() => void) | undefined }) {
    const [_user, { hasPermission }, roles] = useAuthContext();
    const hasRemove = hasPermission(Permission.REMOVE_USER);
    const hasModify = hasPermission(Permission.MODIFY_USER);
    return (
        <div class="item"
             classList={{ "deleted": props.user.isDeleted }}>
            <div class="flex flex-col flex-grow basis-1/3 gap-1 flex-wrap">
                <p>{props.user.lastName} {props.user.firstName}</p>
                <A href={`/users/${props.user.id}`} class="link">@{props.user.userName}</A>
            </div>
            <div
                class="flex flex-row max-sm:basis-full basis-1/2 flex-wrap max-sm:flex-grow max-sm:place-content-center place-content-end gap-1 self-center justify-self-end">
                <Button class="button secondary small max-sm:basis-full" disabled={!hasModify()}>Изменить</Button>
                <DeleteButton onConfirm={props.onDelete} disabled={!hasRemove()} />
            </div>
        </div>
    );
}

// TODO: REMOVE INDICATIONS
// TODO: EDIT
function Roles() {
    const queryClient = useQueryClient();
    const pages = createQuery(() => ({
        queryKey: ["rolesPages"],
        queryFn: async () => await ApiClient.instance.roles.totalPages()
    }));
    const [page, setPage] = createSignal(1);
    const roles = createQuery(() => ({
        queryKey: ["roles", page()],
        queryFn: async () => {
            return await ApiClient.instance.roles.getAll(page());
        }
    }));
    return (
        <div class="flex flex-col gap-4 justify-items-center">
            <Switch>
                <Match when={roles.isLoading}>
                    <Icon code={"\ue9d0"} size={"3rem"} class="animate-spin" />
                </Match>
                <Match when={roles.isFetched}>
                    <For each={roles.data}>{(item, index) => (
                        <Role role={item}
                              onDelete={() => item.id && ApiClient.instance.roles.delete(item.id).then(value => {
                                  value.status == 204 && queryClient.invalidateQueries({
                                      queryKey: ["roles", page()]
                                  });
                              })} />
                    )}</For>
                    <Show when={pages.isFetched && pages.data! > 1}>
                        <PaginationComponent totalPages={pages.data!} page={page()} onPageChange={setPage} />
                    </Show>
                </Match>
            </Switch>
        </div>
    );
}

function Role(props: { role: role, onDelete: (() => void) | undefined }) {
    const [_user, { hasPermission }, _roles] = useAuthContext();
    const navigate = useNavigate();
    const hasRemove = hasPermission(Permission.REMOVE_ROLE);
    const hasModify = hasPermission(Permission.MODIFY_ROLE);
    return (
        <div class="item"
             classList={{ "deleted": props.role.isDeleted }}>
            <p>{props.role.name}</p>
            <div
                class="flex flex-row max-sm:basis-full basis-1/2 flex-wrap max-sm:flex-grow max-sm:place-content-center place-content-end gap-1 self-center justify-self-end">
                <Button class="button secondary small max-sm:basis-full"
                        onClick={() => navigate(`/control/edit/role/${props.role.id}`, { state: { role: props.role } })}
                        disabled={!hasModify()}>Изменить</Button>
                <DeleteButton onConfirm={props.onDelete} disabled={!hasRemove()} />
            </div>
        </div>
    );
}

function Posts() {
    const queryClient = useQueryClient();
    const pages = createQuery(() => ({
        queryKey: ["postsPages"],
        queryFn: async () => await ApiClient.instance.posts.totalPages()
    }));
    const [page, setPage] = createSignal(1);
    const posts = createQuery(() => ({
        queryKey: ["posts", page()],
        queryFn: async () => {
            return await ApiClient.instance.posts.getAll(page());
        }
    }));
    return (
        <div class="flex flex-col gap-4 justify-items-center">
            <Switch>
                <Match when={posts.isLoading}>
                    <Icon code={"\ue9d0"} size={"3rem"} class="animate-spin" />
                </Match>

                <Match when={posts.isFetched}>
                    <For each={posts.data}>{(item, index) => (
                        <Post post={item}
                              onDelete={() => item.id && ApiClient.instance.posts.delete(item.id).then(value => {
                                  value.status == 204 && queryClient.invalidateQueries({
                                      queryKey: ["posts", page()]
                                  });
                              })} />
                    )}</For>
                    <Show when={pages.isFetched && pages.data! > 1}>
                        <PaginationComponent totalPages={pages.data!} page={page()} onPageChange={setPage} />
                    </Show>
                </Match>

            </Switch>
        </div>
    );
}

function Post(props: { post: post, onDelete: (() => void) | undefined }) {
    const [_user, { hasPermission }, _roles] = useAuthContext();
    const hasRemove = hasPermission(Permission.REMOVE_POST);
    const hasModify = hasPermission(Permission.MODIFY_POST);
    console.log(props.post);
    return (
        <div class="item"
             classList={{ "deleted": props.post.isDeleted }}>
            <div class="flex flex-col flex-grow basis-1/3 gap-1 flex-wrap">
                <p>{new Date(props.post.creationDate).toLocaleString()}</p>
                <A href={`/users/${props.post.user.id}`} class="link">@{props.post.user.userName}</A>
            </div>
            <div
                class="flex flex-row max-sm:basis-full basis-1/2 flex-wrap max-sm:flex-grow max-sm:place-content-center place-content-end gap-1 self-center justify-self-end">
                <Button class="button secondary small max-sm:basis-full" disabled={!hasModify()}>Изменить</Button>
                <DeleteButton onConfirm={props.onDelete} disabled={!hasRemove()} />
            </div>
        </div>
    );
}

function DeleteButton(props: { onConfirm?: () => void, onDeny?: () => void, disabled?: boolean }) {
    const [open, setOpen] = createSignal(false);
    return (
        <DialogComponent onOpenChange={setOpen} open={open()}
                         trigger={"Удалить"}
                         disabled={props.disabled}
                         triggerClass="button error small max-sm:basis-full"
                         title="Подтверждение">
            <p>Вы действительно хотите удалить?</p>
            <div class="flex flex-row gap-2 justify-between">
                <Button class="button error" onClick={() => {
                    setOpen(false);
                    props.onConfirm && props.onConfirm();
                }}>Да</Button>
                <Button class="button secondary" onClick={() => {
                    setOpen(false);
                    props.onDeny && props.onDeny();
                }}>Нет</Button>
            </div>
        </DialogComponent>
    );
}