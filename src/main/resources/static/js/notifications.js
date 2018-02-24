var ROLEPLAYER_NOTIFICATIONS_KEY = "roleplayerNotifications";

function notificationsSupported() {
    return (typeof(Storage) !== "undefined" && ("Notification" in window));
}

function requestNotificationPermission(successCallback, failCallback) {
    if (Notification.permission === "granted") {
        successCallback();
    } else if (Notification.permission !== "denied") {
        Notification.requestPermission(function (permission) {
            if (permission === "granted") {
                successCallback();
            } else {
                failCallback();
            }
        });
    } else {
        return failCallback();
    }
}

function getNotifications() {
    var notifications = JSON.parse(localStorage.getItem(ROLEPLAYER_NOTIFICATIONS_KEY));
    if (notifications) {
        return notifications;
    }
    return {
        zoneNotifications: {},
        playerNotifications: {}
    };
}

function saveNotifications(notifications) {
    localStorage.setItem(ROLEPLAYER_NOTIFICATIONS_KEY, JSON.stringify(notifications));
    renderNotificationList();
}

function createZoneNotification(zoneId, zoneName, minPlayers) {
    var notification = {
        zoneId: zoneId,
        zoneName: zoneName,
        minPlayers: minPlayers
    };
    var notifications = getNotifications();
    notifications.zoneNotifications[new Date().valueOf()] = notification;
    saveNotifications(notifications);
}

function createPlayerNotification(playerNick) {
    var notification = {
        playerNick: playerNick
    };
    var notifications = getNotifications();
    notifications.playerNotifications[new Date().valueOf()] = notification;
    saveNotifications(notifications);
}

function removeNotification(id) {
    var notifications = getNotifications();
    delete notifications.zoneNotifications[id];
    delete notifications.playerNotifications[id];
    saveNotifications(notifications);
}

function renderNotificationList() {
    var notifications = getNotifications();
    var notificationsListTemplate = $("#notifications-list-template").html();
    if (notificationsListTemplate) {
        var rendered = "";
        if (Object.keys(notifications.zoneNotifications).length > 0 || Object.keys(notifications.playerNotifications).length > 0) {
            var compiledTemplate = Handlebars.compile(notificationsListTemplate);
            rendered = compiledTemplate(notifications);
        }
        $("#notifications-list").html(rendered);
    }
}

function triggerNotifications(lastRoleplayerList, currentRoleplayersList) {
    var notifications = getNotifications();
    for (var key in notifications.zoneNotifications) {
        var zoneNotification = notifications.zoneNotifications[key];
        var currentMatchingDimension = getMatchingDimension(currentRoleplayersList, zoneNotification);
        if (currentMatchingDimension !== undefined) {
            var lastMatchingDimension = getMatchingDimension(lastRoleplayerList, zoneNotification);
            if (lastMatchingDimension === undefined) {
                new Notification("SWL: there are " + currentMatchingDimension.roleplayers.length +
                    " roleplayers in same dimension in " + currentMatchingDimension.playfield.name);
            }
        }
    }
    for (var key in notifications.playerNotifications) {
        var playerNotification = notifications.playerNotifications[key];
        var currentMatchingRoleplayer = getMatchingRoleplayer(currentRoleplayersList, playerNotification);
        if (currentMatchingRoleplayer !== undefined) {
            var lastMatchingRoleplayer = getMatchingRoleplayer(lastRoleplayerList, playerNotification);
            if (lastMatchingRoleplayer === undefined) {
                new Notification("SWL: " + playerNotification.playerNick + " has come online");
            }
        }
    }
}

function getMatchingDimension(roleplayersList, zoneNotification) {
    var matchedZone = roleplayersList.find(function (zone) {
        return zone.playfield.playfieldId === zoneNotification.zoneId;
    });
    if (matchedZone) {
        return matchedZone.playfieldInstances.find(function (dimension) {
            return dimension.roleplayers.length >= zoneNotification.minPlayers;
        });
    }
    return undefined;
}

function getMatchingRoleplayer(roleplayersList, playerNotification) {
    return roleplayersList.find(function (zone) {
        return zone.playfieldInstances.find(function (dimension) {
            return dimension.roleplayers.find(function (roleplayer) {
                return roleplayer.nick === playerNotification.playerNick;
            })
        })
    });
}