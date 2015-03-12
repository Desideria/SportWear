
__author__ = 'Oohcar'

import base64
from flask import Flask, jsonify, request, render_template

app = Flask(__name__)


# Default Part
@app.route('/')
def index():
    return render_template('user_index.html')

# Error Task
user_error_task = [
    {
        'msg': '0',
        'errorCode': '0000',
        'errorMsg': 'Param is empty'
    },
    {
        'msg': '0',
        'errorCode': '0100',
        'errorMsg': 'Name or Password is empty.'
    },
    {
        'msg': '0',
        'errorCode': '0101',
        'errorMsg': 'Duplicate User Name.'
    },
    {
        'msg': '0',
        'errorCode': '0102',
        'errorMsg': 'Password should be at least 6 characters.'
    },
    {
        'msg': '0',
        'errorCode': '0110',
        'errorMsg': 'User is not register.'
    },
    {
        'msg': '0',
        'errorCode': '0120',
        'errorMsg': 'User Name is empty.'
    },
    {
        'msg': '0',
        'errorCode': '0121',
        'errorMsg': 'User has no info'
    },
    {
        'msg': '0',
        'errorCode': '0130',
        'errorMsg': 'User_ID or Friend_ID is empty.'
    }]


# User Module
user_set = {'admins': "admins"}

user_infos = {
    "admins":
    {
        'user_id': '0001',
        'user_name': 'admin',
        'user_bio': 'ooOOoo',
        'user_sport':
            {
                'date': '20150201',
                'total': '1000',
                'running': '600',
                'walking': '200',
                'cycling': '200'
            }
    }
}

friend_lists = {'admins': ['roy', 'tom', 'cat']}

friend_detail_lists = {'admins': [
    {
        "user_id": '0002',
        "user_name": 'roy',
        "user_bio": 'hello roy',
        "user_latitude": 90.1111,
        "user_longitude": 120.2222
    },
    {
        "user_id": '0003',
        "user_name": 'tom',
        "user_bio": 'hello tom',
        "user_latitude": 90.1122,
        "user_longitude": 120.2233
    },
    {
        "user_id": '0004',
        "user_name": 'cat',
        "user_bio": 'hello cat',
        "user_latitude": 90.1133,
        "user_longitude": 120.2244
    }
]}

user_detail_info = {
    'admins': {
        'user_bio': 'ooOOoo',
        'user_sex': 1,
        'user_age': 20,
        'user_weight': 70,
        'user_height': 180
    }
}


# 1.User Register Service
@app.route('/v1/user/register', methods=['POST'])
def user_register():
    if not request.args:
        return jsonify(user_error_task[0])
    if not'user_name' in request.args or not'user_pwd' in request.args:
        return jsonify(user_error_task[1])
    u_name = request.args['user_name']
    u_pwd = request.args['user_pwd']
    if u_name in user_set.keys():
        return jsonify(user_error_task[2])
    if len(u_pwd) < 6:
        return jsonify(user_error_task[3])
    else:
        user_set.setdefault(u_name, u_pwd)
    token = base64.b64encode(u_name + " " + u_pwd)
    success_task = {'msg': '1', 'token': token}
    return jsonify(success_task)


# 2.User Login Service
@app.route('/v1/user/login', methods=['POST'])
def user_login():
    if not request.args:
        return jsonify(user_error_task[0])
    if not'user_name' in request.args or not'user_pwd' in request.args:
        return jsonify(user_error_task[1])
    u_name = request.args['user_name']
    u_pwd = request.args['user_pwd']
    for k, v in user_set.iteritems():
        if k == u_name and v == u_pwd:
            token = base64.b64encode(u_name + " " + u_pwd)
            success_task = {'msg': '1', 'token': token}
            return jsonify(success_task)
    return jsonify(user_error_task[4])


# 3.User View Info Service
@app.route('/v1/user/view_info', methods=['GET'])
def user_info():
    if not request.args:
        return jsonify(user_error_task[0])
    if not'user_name' in request.args:
        return jsonify(user_error_task[5])
    v_name = request.args['user_name']
    for k, v in user_infos.iteritems():
        if k == v_name:
            success_task = {'msg': '1', 'info': v}
            return jsonify(success_task)
    return jsonify(user_error_task[6])


# 4.User Add Friends Service
@app.route('/v1/user/add_friend', methods=['POST'])
def user_add_friend():
    success_task = {'msg': '1'}
    if not request.args:
        return jsonify(user_error_task[0])
    if not'user_id' in request.args or not'friend_id' in request.args:
        return jsonify(user_error_task[7])
    user_id = request.args['user_id']
    friend_id = request.args['friend_id']
    for k, v in friend_lists.iteritems():
        if k == user_id:
            if friend_id not in v:
                v.append(friend_id)
            return jsonify(success_task)
    friend_lists[user_id] = friend_id
    return jsonify(success_task)


# 5.User Modify Info Service
@app.route('/v1/user/modify_info', methods=['POST'])
def user_modify_info():
    success_task = {'msg': '1'}
    if not request.args:
        return jsonify(user_error_task[0])
    if not'user_id' in request.args:
        return jsonify(user_error_task[1])
    user_id = request.args['user_id']
    user_bio = request.args['user_bio']
    user_sex = request.args['user_age']
    user_weight = request.args['user_weight']
    user_height = request.args['user_height']
    for k, v in user_detail_info.iteritems():
        if k == user_id:
            if user_bio is not None:
                v['user_bio'] = user_bio
            v['user_sex'] = user_sex
            v['user_weight'] = user_weight
            v['user_height'] = user_height
            return jsonify(success_task)
    return jsonify({
        'msg': '0',
        'errorCode': '0150',
        'errorMsg': 'No This User\'s info'})


# 6.User Modify Password Service
@app.route('/v1/user/modify_pwd', methods=['POST'])
def user_modify_pwd():
    success_task = {'msg': '1'}
    if not request.args:
        return jsonify(user_error_task[0])
    if not'user_id' in request.args:
        return jsonify(user_error_task[1])
    user_name = request.args['user_id']
    old_pwd = request.args['old_pwd']
    new_pwd = request.args['new_pwd']
    for k, v in user_set.iteritems():
        if k == user_name:
            if old_pwd != v:
                return jsonify({
                    'msg': '0',
                    'errorCode': '0161',
                    'errorMsg': 'The Old Password is wrong. '})
            if old_pwd != new_pwd:
                if len(new_pwd) < 6:
                    return jsonify(user_error_task[3])
                else:
                    user_set[user_name] = new_pwd
                    return jsonify(success_task)
            else:
                return jsonify({'msg': '0',
                                'errorCode': '0160',
                                'errorMsg': 'The New Password is equal to the Old One.'})
    return jsonify({'msg': '0',
                    'errorCode': '0161',
                    'errorMsg': 'No this User.'})


# 7.User Get Friends List Service
@app.route('/v1/user/list_friend', methods=['GET'])
def user_get_friend():
    if not request.args:
        return jsonify(user_error_task[0])
    if not'user_id' in request.args:
        return jsonify(user_error_task[7])
    user_id = request.args['user_id']
    for k, v in friend_detail_lists.iteritems():
        if k == user_id:
            success_task = {'msg': '1', "count": 3, "friend_list": v}
            return jsonify(success_task)
    return jsonify({'msg': '1', "count": 0, "friend_list": []})


# 8.User Delete Friend Service
@app.route('/v1/user/del_friend', methods=['POST'])
def user_delete_friend():
    success_task = {'msg': '1'}
    if not request.args:
        return jsonify(user_error_task[0])
    if not'user_id' in request.args or not'friend_id' in request.args:
        return jsonify({
            'msg': '0',
            'errorCode': '0180',
            'errorMsg': 'User ID or Friend ID is None.'
        })
    user_id = request.args['user_id']
    friend_id = request.args['friend_id']
    for k, v in friend_lists.iteritems():
        if k == user_id:
            if friend_id in v:
                v.remove(friend_id)
                return jsonify(success_task)
    return jsonify(success_task)

# Sport Module


# 1. Get Daily Sport Report
daily_success_task = {
    'msg': '1',
    'daily': {
        'total': 4000,
        'current': 3500,
        'walking': {
            'active': '1',
            'calories': '1000',
            'time': 2,
            'distance': '800',
            'step': '981',
            'velocity': '6',
            'route': [
                {
                    'longitude': 120.1112,
                    'latitude': 90.1112
                },
                {
                    'longitude': 120.1113,
                    'latitude': 90.1113
                }],
            'moment': [
                {
                    'period': '8',
                    'calories': '400'
                },
                {
                    'period': '12',
                    'calories': '450'
                }]
        },
        'running': {
            'active': '1',
            'calories': '2000',
            'time': 1.5,
            'distance': '5000',
            'step': '',
            'velocity': '30',
            'route': [
                {
                    'longitude': 120.1112,
                    'latitude': 90.1112
                },
                {
                    'longitude': 120.1113,
                    'latitude': 90.1113
                }],
            'moment': [
                {
                    'period': '14',
                    'calories': '1400'
                },
                {
                    'period': '15',
                    'calories': '1450'
                }]
        },
        'cycling': {
            'active': '0'
        }
    }
}


@app.route('/v1/sport/view_daily', methods=['GET'])
def sport_view_daily():
    if not request.args:
        return jsonify(user_error_task[0])
    if not'user_name' in request.args or not'date' in request.args:
        return jsonify({
            'msg': '0',
            'errorCode': '0210',
            'errorMsg': 'User_name or Date is empty.'
        })
    user_name = request.args['user_name']
    date = request.args['date']
    # check data
    # search database
    return jsonify(daily_success_task)


# 2.Upload Sport Plan
@app.route('/v1/sport/upload_plan', methods=['POST'])
def sport_upload_plan():
    # check data
    return jsonify({
        'msg': '1'
    })


sport_detail_task = {
    'msg': '1',
    'sport_plan': {
        'total': 4000,
        'current': 3500,
        'walking': {
            'active': '1',
            'calories': '1000',
            'time': 2,
            'distance': '800',
            'step': '981',
            'velocity': '6',
            'route': [
                {
                    'longitude': 120.1112,
                    'latitude': 90.1112
                },
                {
                    'longitude': 120.1113,
                    'latitude': 90.1113
                }],
            'moment': [
                {
                    'period': '8',
                    'calories': '400'
                },
                {
                    'period': '12',
                    'calories': '450'
                }]
        },
        'running': {
            'active': '1',
            'calories': '2000',
            'time': 1.5,
            'distance': '5000',
            'step': '',
            'velocity': '30',
            'route': [
                {
                    'longitude': 120.1112,
                    'latitude': 90.1112
                },
                {
                    'longitude': 120.1113,
                    'latitude': 90.1113
                }],
            'moment': [
                {
                    'period': '14',
                    'calories': '1400'
                },
                {
                    'period': '15',
                    'calories': '1450'
                }]
        },
        'cycling': {
            'active': '0'
        },
        'shared': [
            {
                'user_id': '1001',
                'user_name': 'admins',
                'progress': '56'
            },
            {
                'user_id': '1001',
                'user_name': 'ray',
                'progress': '48'
            }
        ]
    }
}


# 3.View User's Sport Plan Detail
@app.route('/v1/sport/list_detail', methods=['GET'])
def sport_view_list_detail():
    # check data
    return jsonify(sport_detail_task)


# 4.View User's Sport Plan List
@app.route('/v1/sport/list', methods=['GET'])
def sport_view_list():
    # check data
    return jsonify({
        'msg': '1',
        'count': 5,
        'list': [
            '0101',
            '0102',
            '0103',
            '0104',
            '0105'
        ]
    })


# 5.Synchronize User's Sport Report
@app.route('/v1/sport/asy', methods=['POST'])
def sport_sync_report():
    return jsonify({
        'msg': '1'
    })


# 6.Delete User's Sport Plan
@app.route('/v1/sport/delete', methods=['POST'])
def sport_delete_plan():
    if not request.args:
        return jsonify(user_error_task[0])
    if not'user_id' in request.args or not'plan_id' in request.args:
        return jsonify({
            'msg': '0',
            'errorCode': '',
            'errorMsg': 'User ID or Plan ID is None.'
        })
    return jsonify({
        'msg': '1'
    })


# 7.Share User's Sport Plan
@app.route('/v1/sport/share', methods=['POST'])
def sport_share_plan():
    if not request.args:
        return jsonify(user_error_task[0])
    if not'user_id' in request.args:
        return jsonify({
            'msg': '0',
            'errorCode': '',
            'errorMsg': 'User ID is None.'
        })
    if not'target_id' in request.args:
        return jsonify({
            'msg': '0',
            'errorCode': '',
            'errorMsg': 'Target ID is None.'
        })
    if not'plan_id' in request.args:
        return jsonify({
            'msg': '0',
            'errorCode': '',
            'errorMsg': 'Plan ID is None.'
        })
    return jsonify({
        'msg': '1'
    })


# 8.Verify Shared Sport Plan
@app.route('/v1/sport/verify_share')
def sport_verify_shared_plan():
    if not request.args:
        return jsonify(user_error_task[0])
    if not'user_id' in request.args:
        return jsonify({
            'msg': '0',
            'errorCode': '',
            'errorMsg': 'User ID is None.'
        })
    if not'target_id' in request.args:
        return jsonify({
            'msg': '0',
            'errorCode': '',
            'errorMsg': 'Target ID is None.'
        })
    if not'plan_id' in request.args:
        return jsonify({
            'msg': '0',
            'errorCode': '',
            'errorMsg': 'Plan ID is None.'
        })
    if not'acceptance' in request.args:
        return jsonify({
            'msg': '0',
            'errorCode': '',
            'errorMsg': 'acceptance flag is None.'
        })
    return jsonify({
        'msg': '1'
    })


# SayHi Module
# 1.Send SayHi
@app.route('/v1/sh/send', methods=['POST'])
def sh_send():
    if not request.args:
        return jsonify(user_error_task[0])
    if not'user_id' in request.args:
        return jsonify({
            'msg': '0',
            'errorCode': '',
            'errorMsg': 'User ID is None.'
        })
    if not'target_id' in request.args:
        return jsonify({
            'msg': '0',
            'errorCode': '',
            'errorMsg': 'Target ID is None.'
        })
    return jsonify({
        'msg': '1'
    })


# 2.Reply SayHi
@app.route('/v1/sh/reply', methods=['POST'])
def sh_reply():
    if not request.args:
        return jsonify(user_error_task[0])
    if not'user_id' in request.args:
        return jsonify({
            'msg': '0',
            'errorCode': '',
            'errorMsg': 'User ID is None.'
        })
    if not'reply_id' in request.args:
        return jsonify({
            'msg': '0',
            'errorCode': '',
            'errorMsg': 'Reply ID is None.'
        })
    return jsonify({
        'msg': '1'
    })


# 3.Get SayHi History
@app.route('/v1/sh/list', methods=['POST'])
def sh_get_list():
    if not request.args:
        return jsonify(user_error_task[0])
    if not'user_id' in request.args:
        return jsonify({
            'msg': '0',
            'errorCode': '',
            'errorMsg': 'User ID is None.'
        })
    return jsonify({
        'msg': '1',
        'count': 5,
        'list': [
            {
                'send_id': '0001',
                'target_id': '0002',
                'status': 0
            },
            {
                'send_id': '0002',
                'target_id': '0001',
                'status': 1
            },
            {
                'send_id': '0003',
                'target_id': '0001',
                'status': 2
            },
            {
                'send_id': '0001',
                'target_id': '0004',
                'status': 2
            },
            {
                'send_id': '0001',
                'target_id': '0002',
                'status': 0
            },
        ]
    })


if __name__ == '__main__':
    app.run()
