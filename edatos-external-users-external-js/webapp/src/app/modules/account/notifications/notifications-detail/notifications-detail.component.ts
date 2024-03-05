import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NoticeService } from '@app/shared/service/user/notice.service';
import { Messages, Notice } from '@app/core/model/notice.model';
import { User } from '@app/core/model';
import { Subscription } from 'rxjs';

@Component({
    selector: 'ac-account-management',
    templateUrl: './notifications-detail.component.html'
})
export class NotificationsDetailComponent implements OnInit, OnDestroy {
    public notice: Notice;
    public account: User;
    private noticeSubscription: Subscription;

    constructor(private noticeService: NoticeService, private activatedRoute: ActivatedRoute) {
    }

    ngOnInit() {
        this.account = this.activatedRoute.snapshot.data.user;
        this.subscribeToNotice();
    }

    ngOnDestroy() {
        this.unsubscribeFromNotice();
    }

    private subscribeToNotice() {
        const urn: string = this.activatedRoute.snapshot.params.id;
        console.log('Notice: ', this.activatedRoute.snapshot.params);
        console.log('Notice: ', this.activatedRoute.snapshot.params.id);
        this.noticeSubscription = this.noticeService.getNotice(urn).subscribe((notice) => {
            this.notice = notice;
            console.log('Notice: ', this.notice);
        });
    }

    private unsubscribeFromNotice() {
        if (this.noticeSubscription) {
            this.noticeSubscription.unsubscribe();
        }
    }


    public joinMessagesText(messages: Messages): string {
        let text = '';
        for (const message of messages.messages) {
            text += message.text;
        }
        return text;
    }

}
