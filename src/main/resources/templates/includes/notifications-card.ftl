<style rel="stylesheet">
    #notifications-list li.list-group-item {
        padding: .75rem 0;
    }
</style>

<div class="card">
    <div class="card-body">
        <h5 class="card-title">Notifications</h5>
        <p id="notifications-desc">Be notified when enough players are in same zone, or a specific player
            comes online. You have to keep this page open to receive notifications.</p>
        <button id="create-notification-open-modal-btn" type="button" class="btn btn-outline-primary">Create notification</button>

    </div>
    <div id="notifications-list" class="card-body">
    </div>
</div>

<#-- Handlebars template for the notifications-list -->
<script id="notifications-list-template" type="text/x-handlebars-template">
    <h6 class="card-title">Active notifications</h6>
    <ul id="" class="list-group list-group-flush">
        {{#each zoneNotifications}}
            <li class="list-group-item">
                <div class="row">
                    <div class="col">RPers in {{this.zoneName}} &ge; {{this.minPlayers}}</div>
                    <div class="col-lg-3 col-xl-2"><button type="button" class="btn btn-sm btn-outline-danger" onclick="removeNotification({{@key}})">X</button></div>
                </div>
            </li>
        {{/each}}
        {{#each playerNotifications}}
            <li class="list-group-item">
                <div class="row">
                    <div class="col">{{this.playerNick}} is online</div>
                    <div class="col-lg-3 col-xl-2"><button type="button" class="btn btn-sm btn-outline-danger" onclick="removeNotification({{@key}})">X</button></div>
                </div>
            </li>
        {{/each}}
    </ul>
</script>

<#-- Modal for creating a notification -->
<#include "create-notification-modal.ftl">


<script type="application/javascript">

    $(function () {
        <#-- Check whether the browser supports notifications -->
        if (!notificationsSupported) {
            $("#notifications-desc").text("Your browser does not support notifications.");
            $("#create-notification-btn").prop("disabled", true);
        }

        renderNotificationList()

        $("#create-notification-open-modal-btn").click(function () {
            requestNotificationPermission(function () {
                $('#create-notification-modal').modal('show');
            }, function () {
                $('#create-notification-modal').modal('hide');
                alert("You have to enable notifications in your browser");
            });
        })
    });
</script>
