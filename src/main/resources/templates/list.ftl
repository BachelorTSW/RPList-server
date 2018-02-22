<!DOCTYPE html>
<html lang="en">
<head>
    <script
            src="https://code.jquery.com/jquery-3.2.1.min.js"
            integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4="
            crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/mustache.js/2.3.0/mustache.js"></script>
    <style type="text/css">
        .playfield-instance-roleplayers {
            border-bottom: 1px solid black;
        }
    </style>
</head>
<body>

<div id="list">
    Loading...
</div>

<script id="listTemplate" type="x-tmpl-mustache">
    <ul class="playfields-list">
        {{#playfields}}
            <li class="playfield">
                <h2>{{playfield.name}}</h2>
                <ul class="playfield-instances">
                    {{#playfieldInstances}}
                        <li class="playfield-instance">
                            <ul class="playfield-instance-roleplayers">
                                {{#roleplayers}}
                                    <li class="playfield-instance-roleplayer">{{firstName}} "{{nick}}" {{lastName}} - AutoMeetup:'{{autoMeetup}}'</li>
                                {{/roleplayers}}
                            </ul>
                        </li>
                    {{/playfieldInstances}}
                </ul>
            </li>
        {{/playfields}}
    </ul>
</script>

<script type="application/javascript">
    var listTemplate = $("#listTemplate").html();
    Mustache.parse(listTemplate);

    setInterval(load, 30000);
    load();

    function load() {
        $.get("list.json", function (data) {
            var rendered = Mustache.render(listTemplate, {playfields: data});
            $("#list").html(rendered);
        });
    }
</script>

</body>
</html>