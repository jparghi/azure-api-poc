
## 🧩 **Core Components of Angular (High-level Overview)**

Angular is a **component-based framework** built on TypeScript.
It’s designed around **modular, declarative UI + dependency injection + reactive programming.**

Here are the **core concepts (pillars)** that make up any Angular application:

---

### 1️⃣ **Components**

✅ *What they are:*
The **building blocks** of the UI.
Each component controls a view (HTML + CSS + TypeScript logic).

```ts
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  title = 'Dashboard';
}
```

```html
<h1>{{ title }}</h1>
```

🔹 **AppComponent** → root component
🔹 Other components → form pages, lists, tables, modals, etc.

---

### 2️⃣ **Templates**

✅ *What they are:*
HTML views enriched with Angular syntax (directives, bindings).
They define **what the user sees**.

**Examples of template features:**

```html
<p>{{user.name}}</p>             <!-- Interpolation -->
<button (click)="save()">Save</button> <!-- Event binding -->
<div *ngIf="isAdmin">Welcome</div>     <!-- Structural directive -->
```

---

### 3️⃣ **Directives**

✅ *What they are:*
Instructions that **extend or manipulate** DOM behavior or appearance.

| Type           | Example                                   | Purpose                               |
| -------------- | ----------------------------------------- | ------------------------------------- |
| **Structural** | `*ngIf`, `*ngFor`                         | Add/remove elements                   |
| **Attribute**  | `[ngClass]`, `[ngStyle]`                  | Change element appearance dynamically |
| **Custom**     | `@Directive({ selector: '[highlight]' })` | Create your own behavior              |

---

### 4️⃣ **Services**

✅ *What they are:*
Reusable business logic or data-fetching classes, **not tied to the UI.**
Used via Angular’s **Dependency Injection (DI)** system.

```ts
@Injectable({ providedIn: 'root' })
export class ApiService {
  getUsers() { return this.http.get('/api/users'); }
}
```

Components inject them like:

```ts
constructor(private api: ApiService) {}
```

---

### 5️⃣ **Dependency Injection (DI)**

✅ *What it is:*
Angular’s mechanism to **provide shared instances** of services.
The **Injector** supplies dependencies wherever needed.

This makes code modular, testable, and maintainable.

Example:

```ts
constructor(private logger: LoggerService) {}
```

---

### 6️⃣ **Routing**

✅ *What it is:*
Allows navigation between views **without page reloads** (Single Page App behavior).

```ts
const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'users', component: UsersComponent }
];
```

Template:

```html
<a routerLink="/users">Users</a>
<router-outlet></router-outlet>
```

---

### 7️⃣ **Modules (NgModule)**

✅ *What they are:*
Containers that group related components, directives, and services.
The root is `AppModule`, though in **standalone mode** (like your app), components can bootstrap without modules.

```ts
@NgModule({
  declarations: [AppComponent, DashboardComponent],
  imports: [BrowserModule, FormsModule],
  bootstrap: [AppComponent]
})
export class AppModule {}
```

---

### 8️⃣ **Pipes**

✅ *What they are:*
Transform data in templates.
Think of them as filters for display.

```html
<p>{{ today | date:'short' }}</p>
<p>{{ name | uppercase }}</p>
```

You can also create custom pipes (`@Pipe()`).

---

### 9️⃣ **Observables & RxJS**

✅ *What they are:*
Angular’s reactive programming backbone.
Used heavily in HTTP requests, async data streams, and event handling.

```ts
this.api.getUsers().subscribe(users => this.users = users);
```

---

### 🔟 **Forms (Template-driven / Reactive)**

✅ *What they are:*
Mechanisms to handle user input and validation.

* **Template-driven:** Easier for simple forms (`ngModel`)
* **Reactive forms:** More control, scalable, uses `FormGroup`, `FormControl`.

---

## 🧠 **How They Fit Together**

| Layer         | Core Angular Concept       | Example in Your POC                             |
| ------------- | -------------------------- | ----------------------------------------------- |
| UI            | **Components & Templates** | `DashboardComponent`, HTML tables               |
| Behavior      | **Directives & Pipes**     | `*ngIf`, `DatePipe`                             |
| Logic         | **Services**               | `ApiService` for REST calls                     |
| Communication | **RxJS Observables**       | API data streams                                |
| Navigation    | **Router**                 | `app.routes.ts`                                 |
| Integration   | **DI & Modules**           | `provideHttpClient()`, `bootstrapApplication()` |
| Presentation  | **Styling**                | `styles.css`, card layouts                      |

---