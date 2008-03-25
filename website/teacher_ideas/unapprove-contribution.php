<?php

include_once("../admin/BasePage.php");

class UnapproveContributionPage extends BasePage {

    function update() {
        $this->unapprove_success = false;
        $this->meta_refresh($this->referrer, 2);

        if (isset($_REQUEST['contribution_id'])) {
            $contribution_id = $_REQUEST['contribution_id'];
            $contributor = contributor_get_contributor_by_username(auth_get_username());

            if ($contributor['contributor_is_team_member']) {
                contribution_set_approved($contribution_id, false);
                $this->unapprove_success = true;
            }
        }
    }

    function render_content() {
        if ($this->unapprove_success) {
            print <<<EOT
        <p>The contribution has been marked as unapproved.</p>

EOT;
        }
        else {
            print <<<EOT
        <p>The contribution cannot be marked as unapproved because you do not have permission to do so.</p>

EOT;
        }
    }

}

auth_do_validation();
$page = new UnapproveContributionPage(3, get_referrer("../teacher_ideas/manage-contributions.php"), "Unapprove Contribtuion");
$page->update();
$page->render();

?>