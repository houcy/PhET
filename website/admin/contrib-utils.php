<?php

    include_once("global.php");
    include_once("db.inc");
    include_once("db-utils.php");
    include_once("web-utils.php");
    include_once("sys-utils.php"); 
    include_once("sim-utils.php");  
    
    function contribution_get_files_listing_html($contribution_id) {
        $files_html = '<ul>';
        
        $files = contribution_get_contribution_files($contribution_id);
        
        foreach($files as $file) {
            eval(get_code_to_create_variables_from_array($file));
            
            $basename = basename($contribution_file_url);
            
            $matches = array();
            
            preg_match('/php.*_(.+)/i', $basename, $matches);
            
            $name = $matches[1];
            
            $delete = "<input name=\"delete_contribution_file_id_${contribution_file_id}\" type=\"submit\" value=\"Delete\" >";
            
            $files_html .= "<li>$delete <a href=\"".SITE_ROOT."$contribution_file_url\">$name</a></li>";
        }
        
        $files_html .= "</ul>";
        
        return $files_html;
    }
    
    function contribution_get_simulations_listing_html($contribution_id) {
        $simulations_html = "<ul>";
        
        $simulation_listings = contribution_get_associated_simulation_listings($contribution_id);
                
        foreach($simulation_listings as $simulation_listing) {
            eval(get_code_to_create_variables_from_array($simulation_listing));
            
            $simulation = sim_get_simulation_by_id($sim_id);
            
            eval(get_code_to_create_variables_from_array($simulation));
            
            $delete = "<input name=\"delete_simulation_contribution_id_${simulation_contribution_id}\" type=\"submit\" value=\"Delete\" >";
            
            $simulations_html .= "<li>$delete <a href=\"".SITE_ROOT."simulations/sims.php?sim_id=$sim_id\">$sim_name</a></li>";
        }
        
        $simulations_html .= "</ul>";
        
        return $simulations_html;
    }
    
    function contribution_print_full_edit_form($contribution_id, $script, $referrer, $optional_message = null) {
        $contribution = contribution_get_contribution_by_id($contribution_id);
        
        eval(get_code_to_create_variables_from_array($contribution));
        
        $files_html = contribution_get_files_listing_html($contribution_id);
        
        //$simulations_html = contribution_get_simulations_listing_html($contribution_id);
            
        
        $all_contribution_types = contribution_get_all_template_contribution_types();
        $contribution_types     = contribution_get_contribution_types_for_contribution($contribution_id);
        

        print <<<EOT
            <form id="contributioneditform" method="post" action="$script">
                <fieldset>
                    <legend>Required</legend>
EOT;

        if ($optional_message !== null) {
            print "$optional_message";
        }
        
        print <<<EOT
                    <h3>General</h3>
                    
                    Please describe the contribution:
                    
                    <label for="contribution_authors">
                        authors:
                        
                        <input type="text" name="contribution_authors"
                            value="$contribution_authors" tabindex="1" id="contribution_authors" size="50" />
                    </label>
                    
                    <label for="contribution_title">
                        title:
                        
                        <input type="text" name="contribution_title" 
                            value="$contribution_title" tabindex="1" id="contribution_title" size="50"/>
                    </label>
                    
                    <label for="contribution_keywords">
                        keywords:
                        
                        <input type="text" name="contribution_keywords"
                            value="$contribution_keywords" tabindex="3" id="contribution_keywords" size="50" />
                    </label>
                    
                    <h3>Simulations</h3>

                    <p>Choose the simulations that the contribution was designed for:<p/>
EOT;

        print_multiple_selection(
            sim_get_all_sim_names(),
            contribution_get_associated_simulation_listing_names($contribution_id)
        );
                    
        print <<<EOT
                    <h3>Classification</h3>
                    
                    <p>Choose the type of contribution:</p>
                    
EOT;
        print_multiple_selection($all_contribution_types, $contribution_types);
        
        print <<<EOT
                    <p>Choose the level of the contribution:</p>
                    
EOT;

        print_multiple_selection(
            contribution_get_all_template_level_names(),
            contribution_get_level_names_for_contribution($contribution_id)
        );

        print <<<EOT
                    <h3>Files</h3>
                    
                    <p>Remove existing files from the contribution:</p>
                    
                    $files_html
                    
                    <p>Add any number of files to the contribution:</p>
                    
                    <label for="contribution_file_url">                        
                        <input type="file" name="contribution_file_url" class="multi" />
                    </label>

                    <br/>
                    <br/>
                    <p/>


                </fieldset>
                <fieldset>
                    <legend>Optional</legend>
                                        
                    <label for="contribution_desc">
                        description:
                        
                        <textarea name="contribution_desc" tabindex="4" id="contribution_desc" rows="5" cols="50">$contribution_desc</textarea>
                    </label>
                    
                    <p>Please choose the subject areas covered by the contribution:</p>

EOT;
        
        print_multiple_selection(
            contribution_get_all_template_subject_names(),
            contribution_get_subject_names_for_contribution($contribution_id)
        );
        
        print <<<EOT
                    <label for="contribution_duration">
                        duration:
EOT;

        print_single_selection(
            "contribution_duration",
            array(
                "30"    => "30 minutes",
                "60"    => "60 minutes",
                "120"   => "120 minutes"
            ),
            $contribution_duration
        );

        print <<<EOT
                    </label>
                    
                    <label for="contribution_answers_included">
                        answers included:
EOT;

        print_checkbox(
            "contribution_answers_included",
            "",
            $contribution_answers_included
        );

        print <<<EOT
                    </label>
                    
                    <p>Please describe how the contribution complies with the K-12 National Science Standards:</p>
                    
                    <table>
                        <thead>
                            <tr>
                                <td width="400" height="25" align="right">&nbsp;</td>

                                <td colspan="3" align="center"><span class="style9">Content Level</span></td>
                            </tr>
                        </thead>
                        
                        <tbody>
                            <tr>
                                <td width="400" height="30" align="right"><span class="style9">Content Standard</span></td>

                                <td width="50" height="30" align="center">K-4</td>

                                <td width="50" height="30" align="center">5-8</td>

                                <td width="50" height="30" align="center">9-12</td>
                            </tr>

                            <tr>
                                <td width="400" height="30" align="right"><label>Science as Inquiry - A</label></td>

                                <td width="50" height="30" align="center"><input name="K4A" type="checkbox" id="K4A" value="1"></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="58A" value="8"></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="912A" value="15"></td>
                            </tr>

                            <tr>
                                <td width="400" height="30" align="right"><label>Physical Science - B</label></td>

                                <td width="50" height="30" align="center"><input name="K4B" type="checkbox" id="K4B" value="2"></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="58B" value="9"></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="912B" value="16"></td>
                            </tr>

                            <tr>
                                <td width="400" height="30" align="right"><label>Life Science - C</label></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="K4C" value="3"></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="58C" value="10"></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="912C" value="17"></td>
                            </tr>

                            <tr>
                                <td width="400" height="30" align="right">Earth &amp; Space Science - D</td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="K4D" value="4"></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="58D" value="11"></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="912D" value="18"></td>
                            </tr>

                            <tr>
                                <td width="400" height="30" align="right"><label>Science &amp; Technology - E</label></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="K4E" value="5"></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="58E" value="12"></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="912E" value="19"></td>
                            </tr>

                            <tr>
                                <td width="400" height="30" align="right"><label>Science in Personal and Social Perspective - F</label></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="K4F" value="6"></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="58F" value="13"></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="912F" value="20"></td>
                            </tr>

                            <tr>
                                <td width="400" height="30" align="right">History and Nature of Science - G</td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="K4G" value="7"></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="58G" value="14"></td>

                                <td width="50" height="30" align="center"><input type="checkbox" name="912G" value="21"></td>
                            </tr>
                        </tbody>
                    </table>

                    <input type="hidden" name="referrer"        value="$referrer" />
                    <input type="hidden" name="contribution_id" value="$contribution_id" />
                 </fieldset>
                 
                 <label for="submit">
                     <input name="submit" type="submit" id="submit" tabindex="13" value="Update" />
                 </label>
            </form>
EOT;
    }
    
    function contribution_print_summary($contribution, $contributor_id, $contributor_is_team_member, $referrer = '') {
        eval(get_code_to_create_variables_from_array($contribution));
        
        $path_prefix = SITE_ROOT."teacher_ideas/";
        
        $query_string = "?contribution_id=$contribution_id&amp;referrer=$referrer";
        
        $view    = "<a href=\"${path_prefix}view-contribution.php$query_string\">details</a>";
        $edit    = '';
        $delete  = '';
        $approve = '';
        
        if ($contributor_id !== null && contribution_can_contributor_manage_contribution($contributor_id, $contribution_id)) {
            $edit   .= ", <a href=\"${path_prefix}edit-contribution.php$query_string\">edit</a>";
            $delete .= ", <a href=\"${path_prefix}delete-contribution.php$query_string\">delete</a>";
        
            if ($contributor_is_team_member) {
                if ($contribution_approved) {
                    $approve .= ", <a href=\"${path_prefix}unapprove-contribution.php$query_string\">unapprove</a>";
                }
                else {
                    $approve .= ", <a href=\"${path_prefix}approve-contribution.php$query_string\">approve</a>";
                }
            }
        }
        
        $contribution_files = contribution_get_contribution_files($contribution_id);
        
        if (count($contribution_files) == 1) {
            $contribution_link = SITE_ROOT.$contribution_files[0]['contribution_file_url'];
        }
        else {
            $contribution_link = "${path_prefix}view-contribution.php$query_string";
        }
        
        print "<li><a href=\"$contribution_link\">$contribution_title</a> - ($view$edit$delete$approve)</li>";        
    }
    
    function contribution_get_contribution_files($contribution_id) {
        $contribution_files = array();
        
        $contribution_file_rows = run_sql_statement("SELECT * FROM `contribution_file` WHERE `contribution_id`='$contribution_id' ");
        
        while ($contribution = mysql_fetch_assoc($contribution_file_rows)) {
            $contribution_files[] = $contribution;
        }
        
        return $contribution_files;
    }
    
    function contribution_can_contributor_manage_contribution($contributor_id, $contribution_id) {
        $contribution = contribution_get_contribution_by_id($contribution_id);
        $contributor  = contributor_get_contributor_by_id($contributor_id);        
        
        return $contribution['contributor_id'] == $contributor_id || $contributor['contributor_is_team_member'] == '1';
    }
    
    function contribution_get_manageable_contributions_for_contributor_id($contributor_id) {
        $contributions = contribution_get_all_contributions();
        
        foreach($contributions as $index => $contribution) {
            if (!contribution_can_contributor_manage_contribution($contributor_id, $contribution['contribution_id'])) {
                unset($contributions[$index]);
            }
        }
        
        return $contributions;
    }
    
    function contribution_delete_contribution($contribution_id) {
        $condition = array( 'contribution_id' => $contribution_id );
        
        delete_row_from_table('contribution',            $condition);
        delete_row_from_table('contribution_file',       $condition);
        delete_row_from_table('simulation_contribution', $condition);
        delete_row_from_table('contribution_type',       $condition);
        delete_row_from_table('contribution_level',      $condition);
        delete_row_from_table('contribution_subject',    $condition);        
        delete_row_from_table('contribution_comment',    $condition);            
        delete_row_from_table('contribution_flagging',   $condition);            
        delete_row_from_table('contribution_nomination', $condition);                            
        
        return true;
    }
    
    function contribution_add_new_contribution($contribution_title, $contributor_id, $file_tmp_name, $file_user_name) {    
        if (preg_match('/.+\\.(doc|txt|rtf|pdf|odt)/i', $file_user_name) == 1) {
            $contribution_type = "Activity";
        }
        else if (preg_match('/.+\\.(ppt|odp)/i', $file_user_name) == 1) {
            $contribution_type = "Lecture";
        }
        else {
            $contribution_type = "Support";
        }
        
        $contribution_id = insert_row_into_table(
            'contribution',
            array(
                'contribution_title'        => $contribution_title,
                'contributor_id'            => $contributor_id,
                'contribution_date_created' => date('YmdHis')
            )
        );
        
        if (contribution_add_new_file_to_contribution($contribution_id, $file_tmp_name, $file_user_name) == FALSE) {
            return FALSE;
        }

        $contribution_type_id = insert_row_into_table(
            'contribution_type',
            array(
                'contribution_id'        => $contribution_id,
                'contribution_type_desc' => $contribution_type
            )
        );
        
        return $contribution_id;
    }
    
    function contribution_add_new_file_to_contribution($contribution_id, $file_tmp_name, $file_user_name) {
        $this_dir = dirname(__FILE__);

        $new_file_dir_rel = "admin/uploads/contributions";        
        $new_file_dir_abs = "$this_dir/uploads/contributions";
        
        mkdirs($new_file_dir_abs);
        
        $new_name = basename($file_tmp_name)."_$file_user_name";
        
        $new_file_path_rel = "$new_file_dir_rel/$new_name";
        $new_file_path_abs = "$new_file_dir_abs/$new_name";
        
        if (move_uploaded_file($file_tmp_name, $new_file_path_abs)) {
            $contribution_file_id = insert_row_into_table(
                'contribution_file',
                array(
                    'contribution_id'       => $contribution_id,
                    'contribution_file_url' => $new_file_path_rel
                )
            );
            
            return $contribution_file_id;
        }
        
        return FALSE;
    }
    
    function contribution_associate_contribution_with_simulation($contribution_id, $sim_id) {
        $simulation_contribution_id = insert_row_into_table(
            'simulation_contribution',
            array(
                'sim_id'          => $sim_id,
                'contribution_id' => $contribution_id
            )
        );
        
        return $simulation_contribution_id;
    }

    function contribution_get_all_template_levels() {
        $levels = array();
        
        $contribution_level_rows = run_sql_statement("SELECT * FROM `contribution_level` WHERE `contribution_level_is_template`='1' ORDER BY `contribution_level_desc` ASC ");
        
        while ($contribution_level = mysql_fetch_assoc($contribution_level_rows)) {
            $id = $contribution_level['contribution_level_id'];
            
            $levels["contribution_level_id_$id"] = $contribution_level;
        }
        
        return $levels;
    }
    
    function contribution_get_all_template_level_names() {
        $levels = contribution_get_all_template_levels();
        
        $level_names = array();
        
        foreach($levels as $key => $level) {
            $level_names[$key] = $level['contribution_level_desc'];
        }
        
        return $level_names;
    }
    
    function contribution_get_levels_for_contribution($contribution_id) {
        $levels = array();
        
        $contribution_level_rows = run_sql_statement("SELECT * FROM `contribution_level` WHERE `contribution_id`='$contribution_id' ORDER BY `contribution_level_desc` ASC ");
        
        while ($contribution_level = mysql_fetch_assoc($contribution_level_rows)) {
            $id = $contribution_level['contribution_level_id'];
            
            $levels["contribution_level_id_$id"] = $contribution_level;
        }
        
        return $levels;
    }
    
    function contribution_get_level_names_for_contribution($contribution_id) {
        $levels = contribution_get_levels_for_contribution($contribution_id);
        
        $level_names = array();
        
        foreach($levels as $key => $level) {
            $level_names[$key] = $level['contribution_level_desc'];
        }
        
        return $level_names;
    }
    
    function contribution_get_all_template_subjects() {
        $subjects = array();
        
        $contribution_subject_rows = run_sql_statement("SELECT * FROM `contribution_subject` WHERE `contribution_subject_is_template`='1' ORDER BY `contribution_subject_desc` ASC ");
        
        while ($contribution_subject = mysql_fetch_assoc($contribution_subject_rows)) {
            $id = $contribution_subject['contribution_subject_id'];
            
            $subjects["contribution_subject_id_$id"] = $contribution_subject;
        }
        
        return $subjects;
    }
    
    function contribution_get_all_template_subject_names() {
        $subjects = array();
        
        $contribution_subject_rows = run_sql_statement("SELECT * FROM `contribution_subject` WHERE `contribution_subject_is_template`='1' ORDER BY `contribution_subject_desc` ASC ");
        
        while ($contribution_subject = mysql_fetch_assoc($contribution_subject_rows)) {
            $id = $contribution_subject['contribution_subject_id'];
            
            $subjects["contribution_subject_id_$id"] = $contribution_subject['contribution_subject_desc'];
        }
        
        return $subjects;
    }
    
    function contribution_get_subject_names_for_contribution($contribution_id) {
        $subjects = array();
        
        $contribution_subject_rows = run_sql_statement("SELECT * FROM `contribution_subject` WHERE `contribution_id`='$contribution_id' ORDER BY `contribution_subject_desc` ASC ");
        
        while ($contribution_subject = mysql_fetch_assoc($contribution_subject_rows)) {
            $id = $contribution_subject['contribution_subject_id'];
            
            $subjects["contribution_subject_id_$id"] = $contribution_subject['contribution_subject_desc'];
        }
        
        return $subjects;
    }
    
    function contribution_get_subjects_for_contribution($contribution_id) {
        $subjects = array();
        
        $contribution_subject_rows = run_sql_statement("SELECT * FROM `contribution_subject` WHERE `contribution_id`='$contribution_id' ORDER BY `contribution_subject_desc` ASC ");
        
        while ($contribution_subject = mysql_fetch_assoc($contribution_subject_rows)) {
            $id = $contribution_subject['contribution_subject_id'];
            
            $subjects["contribution_subject_id_$id"] = $contribution_subject;
        }
        
        return $subjects;
    }    
    
    function contribution_get_all_template_contribution_types() {
        $types = array();
        
        $contribution_type_rows = run_sql_statement("SELECT * FROM `contribution_type` WHERE `contribution_type_is_template` = '1' ORDER BY `contribution_type_desc` ASC ");
        
        while ($contribution_type = mysql_fetch_assoc($contribution_type_rows)) {
            $id   = $contribution_type['contribution_type_id'];
            $type = $contribution_type['contribution_type_desc'];
        
            $types["contribution_type_id_$id"] = "$type";
        }
        
        return $types;
    }
    
    function contribution_get_contribution_types_for_contribution($contribution_id) {
        $types = array();
        
        $contribution_type_rows = run_sql_statement("SELECT * FROM `contribution_type` WHERE `contribution_id` = '$contribution_id'  ORDER BY `contribution_type_desc` ASC ");
        
        while ($contribution_type = mysql_fetch_assoc($contribution_type_rows)) {
            $id   = $contribution_type['contribution_type_id'];
            $type = $contribution_type['contribution_type_desc'];
        
            $types["contribution_type_id_$id"] = "$type";
        }
        
        return $types;
    }    

    function contribution_get_associated_simulation_listing_names($contribution_id) {
        $simulation_rows = run_sql_statement("SELECT * FROM `simulation`,`simulation_contribution` WHERE `simulation`.`sim_id`=`simulation_contribution`.`sim_id` AND `simulation_contribution`.`contribution_id`='$contribution_id' ");
        
        $simulations = array();
        
        while ($simulation = mysql_fetch_assoc($simulation_rows)) {
            $sim_id   = $simulation['sim_id'];
            $sim_name = $simulation['sim_name'];
            
            $simulations["$sim_id"] = "$sim_name";
        }
        
        return $simulations;
    }
    
    function contribution_get_associated_simulation_listings($contribution_id) {
        $simulation_rows = run_sql_statement("SELECT * FROM `simulation_contribution` WHERE `contribution_id`='$contribution_id' ");
        
        $simulations = array();
        
        while ($simulation = mysql_fetch_assoc($simulation_rows)) {
            $sim_id = $simulation['sim_id'];
            
            $simulations["$sim_id"] = $simulation;
        }
        
        return $simulations;
    }
    
    function contribution_set_approved($contribution_id, $status) {
        if ($status) {
            $status = '1';
        }
        else {
            $status = '0';
        }
        
        return update_table('contribution', array( 'contribution_approved' => $status ), 'contribution_id', $contribution_id);
    }

    function contribution_get_all_contributions() {
        $contributions = array();
        
        $contribution_rows = run_sql_statement("SELECT * FROM `contribution` ORDER BY `contribution_title` ASC");
        
        while ($contribution = mysql_fetch_assoc($contribution_rows)) {
            $contributions[] = $contribution;
        }
        
        return $contributions;
    }
    
    function contribution_get_all_contributions_for_sim($sim_id) {
        $contributions = array();
        
        $contribution_rows = run_sql_statement("SELECT * FROM `contribution` , `simulation_contribution` WHERE `contribution` . `contribution_id` = `simulation_contribution` . `contribution_id` AND `simulation_contribution` . `sim_id` = '$sim_id' ORDER BY `contribution_title` ASC");
        
        while ($contribution = mysql_fetch_assoc($contribution_rows)) {
            $contributions[] = $contribution;
        }
        
        return $contributions;
    }
    
    function contribution_get_approved_contributions_for_sim($sim_id) {
        $contributions = contribution_get_all_contributions_for_sim($sim_id);
        
        foreach($contributions as $index => $contribution) {
            if ($contribution['contribution_approved'] == '0') {
                unset($contributions[$index]);
            }
        }
        
        return $contributions;
    }
    
    function contribution_get_contribution_by_id($contribution_id) {
        $contribution_rows = run_sql_statement("SELECT * FROM `contribution` WHERE `contribution_id`='$contribution_id' ");
        
        return mysql_fetch_assoc($contribution_rows);
    }
    
    function contributor_get_all_contributors() {
        $contributors = array();
        
        $contributor_rows = 
        run_sql_statement("SELECT * from `contributor` ORDER BY `contributor_name` ASC ");
        
        while ($contributor = mysql_fetch_assoc($contributor_rows)) {
            $contributors[] = $contributor;
        }
        
        return $contributors;
    }
    
    function contributor_is_contributor($username) {
        $contributors = contributor_get_all_contributors();
        
        foreach($contributors as $contributor) {
            if (strtolower($contributor['contributor_email']) == strtolower($username)) {
                return true;
            }
        }
        
        return false;
    }

    function contributor_send_password_reminder($username) {
        $contributors = contributor_get_all_contributors();
        
        foreach($contributors as $contributor) {
            if (strtolower($contributor['contributor_email']) == strtolower($username)) {
                $contributor_name     = $contributor['contributor_name'];
                $contributor_password = $contributor['contributor_password'];
                
                mail($username, 
                     "PhET Password Reminder", 
                     "\n".
                     "Dear $contributor_name, \n".
                     "\n".
                     "Your password is \"$contributor_password.\"\n".
                     "\n".
                     "Regards,\n".
                     "\n".
                     "The PhET Team \n",
                
                     "From: The PhET Team <phethelp@colorado.edu>");
            }
        }
    }
    
    function contributor_get_team_members() {
        $admins = array();
        
        $contributor_rows = run_sql_statement("SELECT * from `contributor` WHERE `contributor_is_team_member`='1' ORDER BY `contributor_name` ASC ");
        
        while ($contributor = mysql_fetch_assoc($contributor_rows)) {
            $admins[] = $contributor;
        }
        
        return $admins;
    }
    
    function contributor_is_admin_username($username) {
        $admins = contributor_get_team_members();
        
        foreach($admins as $contributor) {
            if (strtolower($contributor['contributor_email']) == strtolower($username)) {
                return true;
            }
        }
        
        return false;
    }    

    function contributor_get_id_from_username($username) {
        $contributors = contributor_get_all_contributors();
        
        foreach($contributors as $contributor) {
            if (strtolower($contributor['contributor_email']) == strtolower($username)) {
                return $contributor['contributor_id'];
            }
        }
        
        return false;        
    }
    
    function contributor_get_id_from_username_and_password($username, $password) {
        $contributors = contributor_get_all_contributors();
        
        foreach($contributors as $contributor) {
            if (strtolower($contributor['contributor_email']) == strtolower($username)) {
                if ($contributor['contributor_password'] == $password) {
                    return $contributor['contributor_id'];
                }
            }
        }
        
        return false;        
    }
    
    function contributor_get_id_from_username_and_password_hash($username, $password_hash) {
        $contributors = contributor_get_all_contributors();
        
        foreach($contributors as $contributor) {            
            if (strtolower($contributor['contributor_email']) == strtolower($username)) {
                if (md5($contributor['contributor_password']) == $password_hash) {
                    return $contributor['contributor_id'];
                }
            }
        }
        
        return false;        
    }

    function contributor_is_valid_login($username, $password_hash) {
        return contributor_get_id_from_username_and_password_hash($username, $password_hash) !== false;
    }
    
    function contributor_is_valid_admin_login($username, $password_hash) {
        if (!contributor_is_admin_username($username)) return false;
        
        return contributor_is_valid_login($username, $password_hash);
    }
    
    function contributor_add_new_blank_contributor($is_team_member = false) {
        $team_member_field = $is_team_member ? '1' : '0';
        
        run_sql_statement("INSERT INTO `contributor` (`contributor_is_team_member`) VALUES ('$team_member_field') ");
        
        return mysql_insert_id();
    }
    
    function contributor_add_new_contributor($username, $password, $is_team_member = false) {
        $team_member_field = $is_team_member ? '1' : '0';
        
        run_sql_statement("INSERT INTO `contributor` (`contributor_email`, `contributor_password`, `contributor_is_team_member`) VALUES ('$username', '$password', '$team_member_field') ");
        
        return mysql_insert_id();
    }
    
    function contributor_get_contributor_by_id($contributor_id) {
        $result = run_sql_statement("SELECT * FROM `contributor` WHERE `contributor_id`='$contributor_id' ");
        
        return mysql_fetch_assoc($result);
    }
    
    function contributor_print_full_edit_form($contributor_id, $script, $optional_message = null, 
                                              $standard_message = "<p>You may edit your profile information below.</p>") {
                                                  
        $contributor = contributor_get_contributor_by_id($contributor_id);
        
        gather_array_into_globals($contributor);
        
        global $contributor_name,        $contributor_title, $contributor_address,
               $contributor_office,      $contributor_city,  $contributor_state, 
               $contributor_postal_code, $contributor_primary_phone,
               $contributor_secondary_phone, $contributor_fax,
               $contributor_password,    $contributor_receive_email;
               
        $contributor_receive_email_checked = $contributor_receive_email == '1' ? 'checked="checked"' : '';     
        
        print <<<EOT
            <form id="userprofileform" method="post" action="$script">
                <fieldset>
                    <legend>Profile for $contributor_name</legend>
EOT;

        if ($optional_message !== null) {
            print "$optional_message";
        }

        print <<<EOT
                    $standard_message
                    
                    <label for="contributor_password">
                        <input type="password" name="contributor_password" 
                            value="$contributor_password" tabindex="1" id="password" size="25"/>
                        
                        password:
                    </label>
                    
                    <label for="contributor_name">
                        name:
                        
                        <input type="text" name="contributor_name" 
                            value="$contributor_name" tabindex="2" id="name" size="25"/>
                    </label>
                    
                    <label for="contributor_title">
                        title:
                        
                        <input type="text" name="contributor_title"
                            value="$contributor_title" tabindex="3" id="title" size="25" />
                    </label>
                    
                    <label for="contributor_address">
                        address:
                        
                        <input type="text" name="contributor_address"
                            value="$contributor_address" tabindex="4" id="address" size="25" />
                    </label>
                    
                    <label for="contributor_office">
                        office:
                        
                        <input type="text" name="contributor_office"
                            value="$contributor_office" tabindex="5" id="office" size="15" />
                    </label>
                    
                    <label for="contributor_city">
                        city:
                        
                        <input type="text" name="contributor_city"
                            value="$contributor_city" tabindex="6" id="city" size="15" />
                    </label>
                    
                    <label for="contributor_state">
                        state or province:
                    
                        <input type="text" name="contributor_state"
                            value="$contributor_state" tabindex="7" id="state" size="15" />
                    </label>
                    
                    <label for="contributor_postal_code">
                        postal code:
                        
                        <input type="text" name="contributor_postal_code"
                            value="$contributor_postal_code" tabindex="8" id="postal_code" size="15" />
                    </label>
                    
                    
                    <label for="contributor_primary_phone">
                        primary phone:
                        
                        <input type="text" name="contributor_primary_phone"
                            value="$contributor_primary_phone" tabindex="9" id="primary_phone" size="12" />
                    </label>
                    
                    <label for="contributor_secondary_phone">
                        secondary phone:
                    
                        <input type="text" name="contributor_secondary_phone"
                            value="$contributor_secondary_phone" tabindex="10" id="secondary_phone" size="12" />

                    </label>
                    
                    <label for="contributor_fax">
                        <input type="text" name="contributor_fax"
                            value="$contributor_fax" tabindex="10" id="fax" size="11" />

                        fax:
                    </label>
                    
                    <input type="hidden" name="contributor_receive_email" value="0" />
                    
                    <label for="contributor_receive_email">
                        <input type="checkbox" name="contributor_receive_email"
                            value="1" tabindex="12" $contributor_receive_email_checked>
                            
                        receive email from phet:
                    </label>

                    <label for="submit">
                        <input name="Submit" type="submit" id="submit" tabindex="13" value="Done" />
                    </label>
                 </fieldset>
            </form>
EOT;
    }
    
    function contributor_delete_contributor($contributor_id) {
        run_sql_statement("DELETE FROM `contributor` WHERE `contributor_id`='$contributor_id' ");
        
        return true;
    }
    
    function contributor_update_contributor($contributor_id, $update_array) {
        return update_table('contributor', $update_array, 'contributor_id', $contributor_id);
    }
    
    function contributor_update_contributor_from_script_parameters($contributor_id) {
        $params = array();
        
        foreach($_REQUEST as $key => $value) {
            if ("$key" !== "contributor_id") {
                if (preg_match('/contributor_.*/', "$key") == 1) {
                    $params["$key"] = mysql_real_escape_string("$value");
                }
            }
        }
        
        contributor_update_contributor($contributor_id, $params);
    }
    
    function contributor_gather_fields_into_globals($contributor_id) {
        $contributor = contributor_get_contributor_by_id($contributor_id);
        
        foreach($contributor as $key => $value) {
            $GLOBALS["$key"] = "$value";
        }
    }

?>