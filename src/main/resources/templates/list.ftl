<#-- @ftlvariable name="zones" type="java.util.List<com.swl.mod.rplist.enumerated.Playfield>" -->

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>SWL - online roleplayer</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.2.1.min.js" integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4="
            crossorigin="anonymous"></script>
    <style type="text/css">
        #roleplayers-list {
            font-family: Arial, Helvetica, sans-serif;
        }
        #roleplayers-list table {
            border-top: 1px solid black;
            border-bottom: 1px solid black;
            border-collapse: collapse;
            width: 100%;
            margin: 0;
            padding: 0 20px;
        }
        #roleplayers-list th {
            text-align: left;
            padding: 2px 5px;
        }
        #roleplayers-list .cell-zone {
            min-width: 16ex;
        }
        #roleplayers-list .cell-playername {
            min-width: 30ex;
        }
        #roleplayers-list tr.row-dimension-start {
            border-top: 1px solid black;
        }
        #roleplayers-list tr:nth-child(even) td.roleplayer-name, #roleplayers-list tr:nth-child(even) td.roleplayer-automeetup {
            background: #EEE
        }
        #roleplayers-list tr:nth-child(odd) td.roleplayer-name, #roleplayers-list tr:nth-child(odd) td.roleplayer-automeetup {
            background: #FFF
        }
        #roleplayers-list td {
            padding: 2px 5px;
        }
        #roleplayers-list td.roleplayer-automeetup-on {
            color: green;
        }
        #roleplayers-list td.roleplayer-automeetup-off {
            color: darkred;
        }
    </style>
</head>
<body>

<div class="container">
    <h1>SWL - online roleplayers</h1>
    <div class="row">
        <div id="roleplayers-list" class="col"></div>
        <div id="notifications-container" class="col-lg-4 col-xl-4 d-none d-lg-block">
            <#include "includes/notifications-card.ftl">
        </div>
    </div>
</div>

<#-- Loading spinner -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/spin.js/2.3.2/spin.min.js"></script>
<script type="application/javascript">
    var spinner = new Spinner().spin();
    $("#roleplayers-list").append(spinner.el);
</script>

<#-- Other libs -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/handlebars.js/4.0.11/handlebars.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
        integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"
        integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
<script src="/static/js/notifications.js"></script>

<#-- Handlebars template for the roleplayers-list -->
<script id="roleplayers-list-template" type="text/x-handlebars-template">
    <table class="playfields">
        <thead>
        <tr>
            <th class="header-zone">Zone</th>
            <th class="header-dimension">Dimension</th>
            <th class="header-playername">Player name</th>
            <th class="header-automeetup">Automeetup</th>
        </tr>
        </thead>
        <tbody>
            {{#each playfields}}
                {{#each this.playfieldInstances}}
                    {{#each this.roleplayers}}
                        <tr{{#if @first}} class="row-dimension-start"{{/if}}>
                            {{#if @first}}
                                {{#if @../first}}
                                    <td rowspan="{{../../roleplayerCount}}"><div class="cell-zone">{{../../playfield.name}}</div></td>
                                {{/if}}
                                <td rowspan="{{../roleplayers.length}}"><div class="cell-dimension">{{inc @../index}}</div></td>
                            {{/if}}
                            <td class="roleplayer-name"><div class="cell-playername">{{this.firstName}} "{{this.nick}}" {{this.lastName}}</div></td>
                            {{#if this.autoMeetup}}
                                <td class="roleplayer-automeetup roleplayer-automeetup-on"><div class="cell-automeetup">on</div></td>
                            {{else}}
                                <td class="roleplayer-automeetup roleplayer-automeetup-off"><div class="cell-automeetup">off</div></td>
                            {{/if}}
                        </tr>
                    {{/each}}
                {{/each}}

            {{/each}}
        </tbody>
    </table>
</script>

<script type="application/javascript">
    Handlebars.registerHelper("inc", function(value, options) {
        return parseInt(value) + 1;
    });

    var lastRoleplayersList = [];
    var roleplayersListTemplate = $("#roleplayers-list-template").html();
    var compiledTemplate = Handlebars.compile(roleplayersListTemplate);

    setInterval(loadRoleplayerList, 120000);
    loadRoleplayerList();

    function loadRoleplayerList() {
        $.get("list.json", function (data) {
            var rendered = compiledTemplate({playfields: data});
            $("#roleplayers-list").html(rendered);
            triggerNotifications(lastRoleplayersList, data);
            lastRoleplayersList = data;
        });
    }
</script>

</body>
</html>
