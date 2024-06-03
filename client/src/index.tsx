/* @refresh reload */
import { render } from "solid-js/web";

import "@/index.css";
import App from "@/App";
import { Route, Router } from "@solidjs/router";
import Index from "@routes/Index";
import { lazy } from "solid-js";
import { MatchFilters } from "@solidjs/router/dist/types";

const root = document.getElementById("root");

if (import.meta.env.DEV && !(root instanceof HTMLElement)) {
    throw new Error(
        "Root element not found. Did you forget to add it to your index.html? Or maybe the id attribute got misspelled?"
    );
}

const Login = lazy(() => import("@routes/auth/login/Login"));
const Register = lazy(() => import("@routes/auth/register/Register"));
const Confirm = lazy(() => import("@routes/auth/register/Confirm"));
const Posts = lazy(() => import("@routes/posts/Posts"));
const NewPost = lazy(() => import("@routes/posts/NewPost"));
const Control = lazy(() => import("@routes/control/Index"));
const ControlEditRole = lazy(() => import("@routes/control/edit/Role"));

const numberMatchFilter: MatchFilters = {
    id: /^\d+$/
}

render(() => (
        <Router root={App}>
            <Route path="/auth/login" component={Login} />
            <Route path="/auth/register" component={Register} />
            <Route path="/auth/register/confirm" component={Confirm} />
            <Route path="/control" component={Control} />
            <Route path="/control/edit/role/:id" component={ControlEditRole} matchFilters={numberMatchFilter} />
            <Route path="/posts/:id" component={Posts} matchFilters={numberMatchFilter} />
            <Route path="/posts" component={NewPost} />
            <Route path="*404" component={() => <h1>404</h1>} />
            <Route path="/" component={Index} />
        </Router>
), root!);
