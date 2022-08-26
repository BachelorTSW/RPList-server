<#-- @ftlvariable name="zones" type="java.util.List<com.swl.mod.rplist.enumerated.Playfield>" -->
<#assign notificationTypeZone="players-in-zone">
<#assign notificationTypePlayer="players-online">

<div class="modal fade" id="create-notification-modal" tabindex="-1" role="dialog" aria-labelledby="create-notification-modal-title" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="create-notification-modal-title">Create notification</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form>
                    <div class="form-group">
                        <label for="notification-type-select">Notify when</label>
                        <select id="notification-type-select" class="form-control">
                            <option value="${notificationTypeZone}" selected>Enough players are in same zone and dimension</option>
                            <option value="${notificationTypePlayer}">A player comes online</option>
                        </select>
                    </div>

                    <div id="notification-players-in-zone-settings" class="notification-settings-group">
                        <div class="form-group">
                            <label for="notification-zone-select">Zone</label>
                            <select id="notification-zone-select" class="form-control">
                                <#list zones as zone>
                                    <option value="${zone.getPlayfieldId()?string("0")}">${zone.getName()}</option>
                                </#list>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="notification-zone-min-players">Amount of players &ge;</label>
                            <input id="notification-zone-min-players" class="form-control" type="number" value="1"/>
                        </div>
                    </div>
                    <div id="notification-player-online-settings" class="notification-settings-group" style="display: none">
                        <div class="form-group">
                            <label for="notification-player-online-name">Player nickname</label>
                            <input id="notification-player-online-name" class="form-control" type="text"/>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button id="notification-create-button" type="button" class="btn btn-primary">Create</button>
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
            </div>
        </div>
    </div>
</div>

<script type="application/javascript">
    $(function() {
        $("#notification-type-select").change(function () {
            var selectedType = $(this).val();
            $(".notification-settings-group").hide();
            if (selectedType === "${notificationTypeZone}") {
                $("#notification-players-in-zone-settings").show();
            } else if (selectedType === "${notificationTypePlayer}") {
                $("#notification-player-online-settings").show();
            }
        });

        $("#notification-create-button").click(function () {
            var selectedType = $("#notification-type-select").val();
            if (selectedType === "${notificationTypeZone}") {
                var $zoneSelect = $("#notification-zone-select");
                createZoneNotification(
                        Number($zoneSelect.val()),
                        $zoneSelect.find("option:selected").text(),
                        Number($("#notification-zone-min-players").val()));
            } else if (selectedType === "${notificationTypePlayer}") {
                createPlayerNotification($("#notification-player-online-name").val());
            }
            $('#create-notification-modal').modal('hide');
        })
    });
</script>
